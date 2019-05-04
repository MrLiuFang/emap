package com.pepper.controller.emap.app.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.vo.EventListVo;
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
import com.pepper.service.emap.node.NodeService;
import com.pepper.service.emap.node.NodeTypeService;
import com.pepper.service.file.FileService;

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

	@RequestMapping("/list")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object list(Boolean isFinish) {
		Pager<EventList> pager = new Pager<EventList>();
		AdminUser adminUser =  (AdminUser) this.getCurrentUser();
		pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+ "_currentHandleUser", adminUser.getId());
		if(!isFinish) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+ "_status","W");
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
	public Object info(String id) {
		ResultData resultData = new ResultData();
		EventList eventList = this.eventListService.findById(id);
		if(eventList == null) {
			return resultData;
		}
		AdminUser adminUser =  (AdminUser) this.getCurrentUser();
		EventDispatch eventDispatch = this.eventDispatchService.findEventDispatch(eventList.getEventId(),adminUser.getId());
		if(eventDispatch != null) {
		AdminUser dispatchFrom = this.adminUserService.findById(eventDispatch.getDispatchFrom());
			resultData.setData("dispatchDate", eventDispatch.getCreateDate());
			resultData.setData("dispatchFromName", dispatchFrom.getName());
		}
		resultData.setData("warningLevel", eventList.getWarningLevel());
		resultData.setData("status", eventList.getStatus());
		Node node = nodeService.findBySourceCode(eventList.getSourceCode());
		if(node!=null && StringUtils.hasText(node.getNodeTypeId())) {
			resultData.setData("helpList", helpListService.findByNodeTypeId(node.getNodeTypeId()));
		}
		resultData.setData("currentHelpId", eventList.getHelpId());
		resultData.setData("isUrgent", Integer.valueOf(environment.getProperty("warningLevel", "0"))>=eventList.getWarningLevel());
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
		
		ActionList actionList = new ActionList();
		
		actionList.setActionListId(eventList.getId());
		actionList.setEventId(eventList.getEventId());
		actionList.setStatus("P");
		actionList.setImage1(image1);
		actionList.setImage2(image2);
		actionList.setImage3(image3);
		actionList.setVoice1(voice1);
		actionListService.save(actionList);
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
			eventListVo.setIsUrgent(Integer.valueOf(environment.getProperty("warningLevel", "0"))>=eventList.getWarningLevel());
			EventDispatch eventDispatch = this.eventDispatchService.findEventDispatch(eventList.getEventId(),adminUser.getId());
			if(eventDispatch!=null) {
				AdminUser dispatchFrom = this.adminUserService.findById(eventDispatch.getDispatchFrom());
				eventListVo.setAssignHeadPortrait(this.fileService.getUrl(dispatchFrom.getHeadPortrait()));
			}
			returnList.add(eventListVo);
		}
		return returnList;
	}
}
