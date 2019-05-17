package com.pepper.controller.emap.app.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.event.ActionList;
import com.pepper.model.emap.event.EventDispatch;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.HelpList;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.vo.EventListVo;
import com.pepper.model.emap.vo.HelpListVo;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.model.emap.vo.NodeTypeVo;
import com.pepper.model.emap.vo.NodeVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.emap.event.ActionListService;
import com.pepper.service.emap.event.EventDispatchService;
import com.pepper.service.emap.event.EventListService;
import com.pepper.service.emap.event.HelpListService;
import com.pepper.service.emap.map.MapImageUrlService;
import com.pepper.service.emap.map.MapService;
import com.pepper.service.emap.message.MessageService;
import com.pepper.service.emap.node.NodeService;
import com.pepper.service.emap.node.NodeTypeService;
import com.pepper.service.file.FileService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;

@Controller(value="appEventListController")
@RequestMapping("/app/event")
public class EventListController  extends BaseControllerImpl implements BaseController{
//	获取我的待处理工单
//	获取我的已完成工单
//	获取我的已转移工单
//	根据id获取工单详情
//	完成工单
//	工单转移
//	获取发生告警的设备位置图
	@Reference
	private EventListService eventListService;
	
	@Resource
	private Environment environment;
	
	@Resource
	private AdminUserService adminUserService;
	
	@Resource
	private FileService fileService;
	
	@Reference
	private EventDispatchService eventDispatchService;
	
	@Reference
	private NodeService nodeService;
	
	@Reference
	private HelpListService helpListService;
	
	@Reference
	private ActionListService actionListService;
	
	@Reference
	private MapService mapService;
	
	@Reference
	private NodeTypeService nodeTypeService;
	
	@Reference
	private MapImageUrlService mapImageUrlService;
	
	@Reference
	private ValueOperationsService valueOperationsService;
	
	@Reference
	private MessageService messageService;
	

	@RequestMapping("/list")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object list(Boolean isFinish) {
		Pager<EventList> pager = new Pager<EventList>();
		AdminUser adminUser =  (AdminUser) this.getCurrentUser();
		pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+ "_currentHandleUser", adminUser.getId());
		if(!isFinish) {
			List<String> list = new ArrayList<String>();
			list.add("W");
			list.add("A");
			pager.getJpqlParameter().setSearchParameter(SearchConstant.IN+ "_status",list);
		}else {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+ "_status","P");
		}
		pager = eventListService.findNavigator(pager);
		pager.setData("eventList",convertVo(pager.getResults()));
		pager.setResults(null);
		return pager;
	}
	
	
	
	@RequestMapping("/transferList")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object transferList() {
		Pager<EventList> pager = new Pager<EventList>();
		AdminUser adminUser =  (AdminUser) this.getCurrentUser();
		pager = eventListService.transferList(pager, adminUser.getId());
		pager.setData("eventList",convertVo(pager.getResults()));
		pager.setResults(null);
		return pager;
		
	}
	
	@RequestMapping("/info")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object info(String id) throws IOException {
		ResultData resultData = new ResultData();
		EventList eventList = this.eventListService.findById(id);
		if(eventList == null) {
			return resultData;
		}
		AdminUser adminUser =  (AdminUser) this.getCurrentUser();
		EventDispatch eventDispatch = this.eventDispatchService.findEventDispatch(eventList.getId(),adminUser.getId());
		if(eventDispatch != null) {
			AdminUser dispatchFrom = this.adminUserService.findById(eventDispatch.getDispatchFrom());
			if(dispatchFrom==null) {
				resultData.setData("dispatchFromName", "系统自动派单");
			}else {
				resultData.setData("dispatchFromName", dispatchFrom.getName());
			}
			resultData.setData("dispatchDate", eventDispatch.getCreateDate());
			
		}else{
			
		}
		resultData.setData("warningLevel", eventList.getWarningLevel());
		resultData.setData("status", eventList.getStatus());
		Node node = nodeService.findBySourceCode(eventList.getSourceCode());
		ActionList actionList = actionListService.findByActionListId(id);
		if(actionList!=null) {
			resultData.setData("image1", fileService.getUrl(actionList.getImage1()));
			resultData.setData("image2", fileService.getUrl(actionList.getImage2()));
			resultData.setData("image3", fileService.getUrl(actionList.getImage3()));
			resultData.setData("voice1", fileService.getUrl(actionList.getVoice1()));
			resultData.setData("content", actionList.getContent());
		}else {
			resultData.setData("image1", "");
			resultData.setData("image2", "");
			resultData.setData("image3", "");
			resultData.setData("voice1", "");
			resultData.setData("content", "");
		}
		if(node!=null && StringUtils.hasText(node.getNodeTypeId())) {
			List<HelpList> list = helpListService.findByNodeTypeId(node.getNodeTypeId());
			List<HelpListVo> returnList = new ArrayList<HelpListVo>();
			List<String> helpIdList = new ArrayList<String>();
			if(actionList!=null && StringUtils.hasText(actionList.getHelpId())) {
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = objectMapper.readTree(actionList.getHelpId());
				if(jsonNode.isArray()) {
					Iterator<JsonNode> array =jsonNode.iterator();
					while (array.hasNext()) {
						helpIdList.add(array.next().asText());
					}
				}
			}
			
			for(HelpList helpList : list) {
				HelpListVo helpListVo = new HelpListVo();
				BeanUtils.copyProperties(helpList, helpListVo);
				helpListVo.setIsCheck(helpIdList.contains(helpList.getId()));
				returnList.add(helpListVo);
			}
			resultData.setData("helpList", returnList);
		}
		resultData.setData("remark", eventList.getContent());
		resultData.setData("nodeId",node.getId());
		resultData.setData("currentHelpId", eventList.getHelpId());
		resultData.setData("isUrgent", eventList.getWarningLevel()>=Integer.valueOf(environment.getProperty("warningLevel", "0")));
		
		
		return resultData;
	}
	
	@RequestMapping("/finish")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object finish(@RequestBody String str) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(str);
		String id = jsonNode.get("id").asText("");
		String image1 = jsonNode.get("image1").asText("");
		String image2 = jsonNode.get("image2").asText("");
		String image3 = jsonNode.get("image3").asText("");
		String voice1 = jsonNode.get("voice1").asText("");
		ResultData resultData = new ResultData();
		EventList eventList = this.eventListService.findById(id);
		if(eventList == null) {
			return resultData;
		}
		
		eventList.setStatus("P");
		eventListService.update(eventList);
		
		AdminUser user  = (AdminUser) this.getCurrentUser();
		ActionList actionList = new ActionList();
		actionList.setOperator(user.getId());
		actionList.setActionListId(eventList.getId());
		actionList.setEventId(eventList.getEventId());
		actionList.setStatus("P");
		actionList.setImage1(image1);
		actionList.setImage2(image2);
		actionList.setImage3(image3);
		actionList.setVoice1(voice1);
		actionList.setContent(jsonNode.get("content").asText(""));
		if(jsonNode.has("helpId")) {
			actionList.setHelpId(jsonNode.get("helpId").toString());
		}
		actionListService.save(actionList);
		return resultData;
	}
	
	@RequestMapping("/toEmployee")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object toEmployee(@RequestBody String str) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,Object> map = objectMapper.readValue(str, Map.class);
//		String eventId,String employeeId
		ResultData resultData = new ResultData();
		EventList eventList = this.eventListService.findById(map.get("eventId").toString());
		if(eventList==null) {
			resultData.setCode(500001);
			resultData.setMessage("要转移的事件不存在！");
			return resultData;
		}
		eventList.setCurrentHandleUser(map.get("employeeId").toString());
		eventListService.update(eventList);
		
		AdminUser adminuser =  (AdminUser) this.getCurrentUser();
		EventDispatch eventDispatch = new EventDispatch();
		eventDispatch.setEventId(eventList.getEventId());
		eventDispatch.setEventListId(eventList.getId());
		eventDispatch.setOperator(map.get("employeeId").toString());
		eventDispatch.setDispatchFrom(adminuser.getId());
		eventDispatch.setTitle(eventList.getEventName());
		eventDispatchService.save(eventDispatch);
		try {
			String employeeId = map.get("employeeId").toString();
			String deviceId = valueOperationsService.get("userDeviceId_"+employeeId);
			messageService.send(deviceId, "您有新的工单",eventList.getEventName(),eventList.getId());
		}catch (Exception e) {
			// TODO: handle exception
		}
		return resultData;
	}
	
	@RequestMapping("/map")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object map(String id) {
		ResultData resultData = new ResultData();
		EventList eventList = this.eventListService.findById(id);
		if(eventList == null) {
			return resultData;
		}
		Node node = nodeService.findBySourceCode(eventList.getSourceCode());
		if(node!=null) {
			NodeVo nodeVo = new NodeVo();
			BeanUtils.copyProperties(node, nodeVo);
			com.pepper.model.emap.map.Map map = mapService.findById(node.getMapId());
			if(map!=null) {
				MapVo mapVo = new MapVo();
				BeanUtils.copyProperties(map, mapVo);
				nodeVo.setMap(mapVo);
				NodeTypeVo nodeTypeVo = new NodeTypeVo();
				NodeType nodeType = nodeTypeService.findById(node.getNodeTypeId());
				if(nodeType != null) {
					BeanUtils.copyProperties(nodeType, nodeTypeVo);
					nodeTypeVo.setWorkingIconUrl(fileService.getUrl(nodeType.getWorkingIcon()));
					nodeTypeVo.setStopIconUrl(fileService.getUrl(nodeType.getStopIcon()));
					nodeVo.setNodeType(nodeTypeVo);
				}
				mapVo.setMapImageUrl(mapImageUrlService.findByMapId(map.getId()));
			}
			resultData.setData("node", nodeVo);
		}
		return resultData;
	} 
	
	private List<EventListVo> convertVo(List<EventList> list){
		List<EventListVo> returnList  = new ArrayList<EventListVo>();
		AdminUser adminUser =  (AdminUser) this.getCurrentUser();
		for(EventList eventList : list) {
			EventListVo eventListVo = new EventListVo();
			BeanUtils.copyProperties(eventList, eventListVo);
			eventListVo.setIsUrgent(eventList.getWarningLevel()>=Integer.valueOf(environment.getProperty("warningLevel", "0")));
			EventDispatch eventDispatch = this.eventDispatchService.findEventDispatch(eventList.getId(),adminUser.getId());
			if(eventDispatch!=null) {
				AdminUser dispatchFrom = this.adminUserService.findById(eventDispatch.getDispatchFrom());
				if(dispatchFrom!=null) {
					eventListVo.setAssignHeadPortrait(this.fileService.getUrl(dispatchFrom.getHeadPortrait()));
				}else {
					eventListVo.setAssignHeadPortrait("");
				}
			}
			returnList.add(eventListVo);
		}
		return returnList;
	}
}
