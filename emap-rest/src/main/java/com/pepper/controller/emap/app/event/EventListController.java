package com.pepper.controller.emap.app.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.event.ActionList;
import com.pepper.model.emap.event.EventDispatch;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.event.HelpList;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.vo.ActionListVo;
import com.pepper.model.emap.vo.AdminUserVo;
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
import com.pepper.service.emap.event.EventRuleService;
import com.pepper.service.emap.event.HelpListService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.map.MapImageUrlService;
import com.pepper.service.emap.map.MapService;
import com.pepper.service.emap.message.MessageService;
import com.pepper.service.emap.node.NodeService;
import com.pepper.service.emap.node.NodeTypeService;
import com.pepper.service.file.FileService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;
import com.pepper.util.FileUtil;

import it.sauronsoftware.jave.AudioUtils;

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
	
	@Reference
	private EventRuleService eventRuleService;
	
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
	
	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping("/list")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object list(Boolean isFinish) {
		systemLogService.log("App event list", this.request.getRequestURL().toString());
		
		AdminUser adminUser =  (AdminUser) this.getCurrentUser();
		if(!isFinish) {
			Pager<EventList> pager = new Pager<EventList>();
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+ "_currentHandleUser", adminUser.getId());
			pager.getJpqlParameter().setSearchParameter(SearchConstant.NOTIN+ "_status",new String[] {"P","B"});
			pager.getJpqlParameter().setSortParameter("assignDate", "DESC");
			pager = eventListService.findNavigator(pager);
			pager.setData("eventList",convertVo(pager.getResults()));
			pager.setResults(null);
			return pager;
		}else {
			Pager<ActionList> pager1 = new Pager<ActionList>();
			pager1.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+ "_status","B");
			pager1.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+ "_operator",adminUser.getId());
			pager1.getJpqlParameter().setSortParameter("assignDate", "DESC");
			pager1 = actionListService.findNavigator(pager1);
			List<ActionList> list = pager1.getResults();
			List<EventListVo> listEventList = new ArrayList<EventListVo>();
			for(ActionList actionList : list) {
				EventList eventList = this.eventListService.findById(actionList.getEventListId());
//				ActionListVo actionListVo = new ActionListVo();
//				BeanUtils.copyProperties(actionList, actionListVo);
//				actionListVo.setEventList(this.convertVo(eventList));
				if(eventList!=null) {
					EventListVo eventListVo = this.convertVo(eventList);
					eventListVo.setActionListId(actionList.getId());
					listEventList.add(eventListVo);
				}
			}
			pager1.setData("eventList",listEventList);
			pager1.setResults(null);
			return pager1;
		}
		
		
	}
	
	@RequestMapping("/actionList")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object actionList(String id) throws IOException {
		systemLogService.log("App action list", this.request.getRequestURL().toString());
		ResultData resultData = new ResultData();
		ActionList actionList = actionListService.findById(id);
		ActionListVo actionListVo = new ActionListVo();
		BeanUtils.copyProperties(actionList, actionListVo);
		actionListVo.setImageUrl1(this.fileService.getUrl(actionList.getImage1()));
		actionListVo.setImageUrl2(this.fileService.getUrl(actionList.getImage2()));
		actionListVo.setImageUrl3(this.fileService.getUrl(actionList.getImage3()));
		actionListVo.setVoiceUrl1(this.fileService.getUrl(actionList.getVoice1()));
		EventList eventList = this.eventListService.findById(actionList.getEventListId());
		Node node = nodeService.findBySourceCode(eventList.getSourceCode());
		if(node!=null && StringUtils.hasText(node.getNodeTypeId())) {
			actionListVo.setHelpList(convertHelpList(helpListService.findByNodeTypeId(node.getNodeTypeId()),actionList.getHelpId(),actionList.getOperatorHelpId()));
		}
		if(StringUtils.hasText(actionList.getOperator())) {
			AdminUser operatorUser = this.adminUserService.findById(actionList.getOperator());
			if(operatorUser!=null) {
				AdminUserVo adminUserVo = new AdminUserVo();
				BeanUtils.copyProperties(operatorUser, adminUserVo);
				adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(operatorUser.getHeadPortrait()));
				adminUserVo.setPassword("");
				actionListVo.setEmployee(adminUserVo);
			}
		}
		resultData.setData("actionList", actionListVo);
		return resultData;
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
		
		systemLogService.log("App event transfer list", this.request.getRequestURL().toString());
		return pager;
		
	}
	
	@RequestMapping("/info")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object info(String id,String actionId) throws IOException {
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
				resultData.setData("dispatchFromName", Internationalization.getMessageInternationalization(7000002));
			}else {
				resultData.setData("dispatchFromName", dispatchFrom.getName());
			}
			resultData.setData("dispatchDate", eventDispatch.getCreateDate());
			
		}else{
			resultData.setData("dispatchFromName", "");
			resultData.setData("dispatchDate", null);
		}
		resultData.setData("warningLevel", eventList.getWarningLevel());
		resultData.setData("status", eventList.getStatus());
		
		Node node = nodeService.findBySourceCode(eventList.getSourceCode());
		if(node!=null) {
			resultData.setData("nodeId",node.getId());
			resultData.setData("helpList", convertHelpList(helpListService.findByNodeTypeId(node.getNodeTypeId()),null,eventList.getHelpId()));
		}
		
		if(StringUtils.hasText(actionId)) {
			ActionList actionList = actionListService.findById(actionId);
			ActionListVo actionListVo = new ActionListVo();
			BeanUtils.copyProperties(actionList, actionListVo);
			actionListVo.setImageUrl1(this.fileService.getUrl(actionListVo.getImage1()));
			actionListVo.setImageUrl2(this.fileService.getUrl(actionListVo.getImage2()));
			actionListVo.setImageUrl3(this.fileService.getUrl(actionListVo.getImage3()));
			actionListVo.setVoiceUrl1(this.fileService.getUrl(actionListVo.getVoice1()));
			if(node!=null&&StringUtils.hasText(node.getNodeTypeId())) {
				actionListVo.setHelpList(convertHelpList(helpListService.findByNodeTypeId(node.getNodeTypeId()),actionList.getHelpId(),actionList.getOperatorHelpId()));
				
			}
			if(StringUtils.hasText(actionList.getOperator())) {
				AdminUser operatorUser = this.adminUserService.findById(actionList.getOperator());
				if(operatorUser!=null) {
					AdminUserVo adminUserVo = new AdminUserVo();
					BeanUtils.copyProperties(operatorUser, adminUserVo);
					adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(operatorUser.getHeadPortrait()));
					adminUserVo.setPassword("");
					actionListVo.setEmployee(adminUserVo);
				}
			}
			resultData.setData("actionList", actionListVo);
		}
		
		resultData.setData("remark", eventList.getContent());
		resultData.setData("currentHelpId", eventList.getHelpId());
		resultData.setData("isUrgent", eventList.getWarningLevel()>=getUrgentWarningLevel(eventList));
		AdminUser user = (AdminUser) this.getCurrentUser();
		if(StringUtils.hasText(eventList.getCurrentHandleUser())) {
			if(eventList.getCurrentHandleUser().equals(user.getId())) {
				resultData.setData("isMeHandle", true);
			}else {
				resultData.setData("isMeHandle", false);
			}
		}else {
			resultData.setData("isMeHandle", false);
		}
		systemLogService.log("App event info", this.request.getRequestURL().toString());
		return resultData;
	}
	
	
	private List<HelpListVo> convertHelpList(List<HelpList> helpList,String helpId,String helpId1) throws IOException{
		List<HelpListVo> helpListVo = new ArrayList<HelpListVo>();
		List<String> helpIdList = new ArrayList<String>();
		ObjectMapper objectMapper = new ObjectMapper();
		if(StringUtils.hasText(helpId)) {
			try{
				JsonNode jsonNode = objectMapper.readTree(helpId);
				if(jsonNode.isArray()) {
					Iterator<JsonNode> array =jsonNode.iterator();
					while (array.hasNext()) {
						helpIdList.add(array.next().asText());
					}
				}
			}catch (Exception e) {
				
			}
		}
		
		List<String> listHelpId = new ArrayList<String>();
		if(StringUtils.hasText(helpId1)) {
			JsonNode jsonNode = objectMapper.readTree(helpId1);
			if(jsonNode.isArray()) {
				Iterator<JsonNode> array =jsonNode.iterator();
				while (array.hasNext()) {
					listHelpId.add(array.next().asText());
				}
			}
		}
		
		for(HelpList obj : helpList) {
			HelpListVo obj1 = new HelpListVo();
			BeanUtils.copyProperties(obj, obj1);
			obj1.setIsCheck(helpIdList.contains(obj1.getId()));
			if(listHelpId.size()>0) {
				if(listHelpId.contains(obj1.getId())) {
					helpListVo.add(obj1);
				}
			}else {
				helpListVo.add(obj1);
			}
		}
		return helpListVo;
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
		
		eventList.setStatus("B");
		eventListService.update(eventList);
		
		AdminUser user  = (AdminUser) this.getCurrentUser();
		ActionList actionList = new ActionList();
		actionList.setOperator(user.getId());
		actionList.setEventListId(eventList.getId());
		actionList.setEventId(eventList.getEventId());
		actionList.setStatus("B");
		actionList.setOperatorHelpId(eventList.getHelpId());
		actionList.setOperatorContent(eventList.getContent());
		actionList.setImage1(image1);
		actionList.setImage2(image2);
		actionList.setFinishDate(new Date());
		actionList.setAssignDate(eventList.getAssignDate());
		actionList.setImage3(image3);
		actionList.setVoice1(voice1);
		actionList.setContent(jsonNode.get("content").asText(""));
		if(jsonNode.has("helpId")) {
			actionList.setHelpId(jsonNode.get("helpId").toString());
		}
		actionListService.save(actionList);
		
		systemLogService.log("App event finish", this.request.getRequestURL().toString(),str);
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
			resultData.setCode(8000002);
			resultData.setMessage(Internationalization.getMessageInternationalization(8000002));
			return resultData;
		}
		eventList.setAssignDate(new Date());
		eventList.setCurrentHandleUser(map.get("employeeId").toString());
		eventListService.update(eventList);
		
		AdminUser adminuser =  (AdminUser) this.getCurrentUser();
		EventDispatch eventDispatch = new EventDispatch();
		eventDispatch.setEventId(eventList.getEventId());
		eventDispatch.setEventListId(eventList.getId());
		eventDispatch.setOperator(map.get("employeeId").toString());
		eventDispatch.setDispatchFrom(adminuser.getId());
		eventDispatch.setTitle(eventList.getEventName());
		eventDispatch.setAssignDate(new Date());
		
		eventDispatchService.save(eventDispatch);
		try {
			String employeeId = map.get("employeeId").toString();
			String deviceId = valueOperationsService.get("userDeviceId_"+employeeId);
			messageService.send(deviceId, Internationalization.getMessageInternationalization(7000001),eventList.getEventName(),eventList.getId());
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		systemLogService.log("App event to employee", this.request.getRequestURL().toString());
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
	
	private EventListVo convertVo(EventList eventList){
		AdminUser adminUser =  (AdminUser) this.getCurrentUser();
		EventListVo eventListVo = new EventListVo();
		BeanUtils.copyProperties(eventList, eventListVo);
		Node node = this.nodeService.findBySourceCode(eventList.getSourceCode());
		if(node !=null) {
			eventListVo.setNodeName(node.getName());
		}
		eventListVo.setIsUrgent(eventList.getWarningLevel()>=getUrgentWarningLevel(eventList));
		EventDispatch eventDispatch = this.eventDispatchService.findEventDispatch(eventList.getId(),adminUser.getId());
		if(eventDispatch!=null) {
			AdminUser dispatchFrom = this.adminUserService.findById(eventDispatch.getDispatchFrom());
			if(dispatchFrom!=null) {
				eventListVo.setAssignHeadPortrait(this.fileService.getUrl(dispatchFrom.getHeadPortrait()));
			}else {
				eventListVo.setAssignHeadPortrait("");
			}
			eventListVo.setDispatchDate(eventDispatch.getCreateDate());
		}
		return eventListVo;
	}
	
	private List<EventListVo> convertVo(List<EventList> list){
		List<EventListVo> returnList  = new ArrayList<EventListVo>();
		for(EventList eventList : list) {
			returnList.add(convertVo(eventList));
		}
		return returnList;
	}
	
	private Integer getUrgentWarningLevel(EventList eventList) {
		Node node = this.nodeService.findBySourceCode(eventList.getSourceCode());
		EventRule eventRule = this.eventRuleService.findByNodeId(node.getId());
		if(eventRule == null) {
			eventRule = eventRuleService.findByNodeTypeId(node.getNodeTypeId());
		}
		return eventRule==null?0:eventRule.getWarningLevel();
	}
	
	
	@RequestMapping("/toMp3")
	@ResponseBody
	public Object toMp3(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		for (String fileName : files.keySet()) {
			MultipartFile file = files.get(fileName); 
			File source = new File("\\","3.amr");
			FileUtils.copyInputStreamToFile(file.getInputStream(), source);
			File target = new File("\\","‪3.mp3");
			AudioUtils.amrToMp3(source, target);
			FileInputStream io = new FileInputStream(target);
			byte b[] = new byte[(int) target.length()];
			io.read(b);
			String fileId = fileService.addFile(b, "1.mp3");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", fileId);
			map.put("url", fileService.getUrl(fileId));
			io.close();
			resultData.setData(map);
			return resultData;
		}
		return resultData;
	}
}
