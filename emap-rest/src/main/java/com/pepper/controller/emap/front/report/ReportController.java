package com.pepper.controller.emap.front.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventMessage;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.vo.AdminUserVo;
import com.pepper.model.emap.vo.EventListVo;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.model.emap.vo.NodeTypeVo;
import com.pepper.model.emap.vo.NodeVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.emap.department.DepartmentGroupService;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.service.emap.event.ActionListService;
import com.pepper.service.emap.event.EventDispatchService;
import com.pepper.service.emap.event.EventListAssistService;
import com.pepper.service.emap.event.EventListService;
import com.pepper.service.emap.event.EventMessageService;
import com.pepper.service.emap.event.EventRuleService;
import com.pepper.service.emap.event.HelpListService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.map.MapImageUrlService;
import com.pepper.service.emap.map.MapService;
import com.pepper.service.emap.message.MessageService;
import com.pepper.service.emap.node.NodeService;
import com.pepper.service.emap.node.NodeTypeService;
import com.pepper.service.emap.staff.StaffService;
import com.pepper.service.file.FileService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;

@Controller()
@RequestMapping(value = "/front/report")
public class ReportController extends BaseControllerImpl  implements BaseController {
	
	@Reference
	private EventListService eventListService;
	
	@Reference
	private EventListAssistService eventListAssistService;
	
	@Reference
	private EventRuleService eventRuleService;
	
	@Reference
	private EventDispatchService eventDispatchService;
	
	@Resource
	private Environment environment;
	
	@Resource
	private NodeService nodeService;
	
	@Resource
	private HelpListService helpListService;
	
	@Resource
	private MapService mapService;
	
	@Resource
	private MapImageUrlService mapImageUrlService;
	
	@Resource
	private NodeTypeService nodeTypeService;
	
	@Resource
	private AdminUserService adminUserService;
	
	@Resource
	private FileService fileService;
	
	@Reference
	private ValueOperationsService valueOperationsService;
	
	@Reference
	private MessageService messageService;
	
	@Reference
	private ActionListService actionListService;
	
	@Reference
	private SystemLogService systemLogService;
	
	@Reference
	private DepartmentService departmentService;
	
	@Reference
	private DepartmentGroupService departmentGroupService;
	
	@Reference
	private StaffService staffService;
	
	@Reference
	private EventMessageService eventMessageService;

	@RequestMapping(value = "/event")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object event(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date eventStartDate, @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event,Integer warningLevel,String node,String nodeTypeId,String mapName,String buildName,String siteName,String operatorId,String status ,String employeeId) {
		systemLogService.log("event report ", this.request.getRequestURL().toString());
		return findEvent(eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId, mapName, buildName, siteName, operatorId, status, employeeId);
	}
	
	@RequestMapping(value = "/openDoor")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object openDoor(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date eventStartDate,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event,Integer warningLevel,String node,String nodeTypeId,String mapName,String buildName,String siteName,String operatorId,String status ,String employeeId) {
		systemLogService.log("event report ", this.request.getRequestURL().toString());
		return findEvent(eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId, mapName, buildName, siteName, operatorId, status, employeeId);
	}
	
	@RequestMapping(value = "/nodeEvent")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object nodeEvent(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date eventStartDate,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event,Integer warningLevel,String node,String nodeTypeId,String mapName,String buildName,String siteName,String operatorId,String status ,String employeeId) {
		systemLogService.log("event report ", this.request.getRequestURL().toString());
		return findEvent(eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId, mapName, buildName, siteName, operatorId, status, employeeId);
	}
	
	@RequestMapping(value = "/employeeHandleEvent")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object employeeHandleEvent(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date eventStartDate,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event,Integer warningLevel,String node,String nodeTypeId,String mapName,String buildName,String siteName,String operatorId,String status ,String employeeId) {
		systemLogService.log("event report ", this.request.getRequestURL().toString());
		return findEvent(eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId, mapName, buildName, siteName, operatorId, status, employeeId);
	}
	
	private Object findEvent(Date eventStartDate, Date eventEndDate, String event,Integer warningLevel,String node,String nodeTypeId,String mapName,String buildName,String siteName,String operatorId,String status ,String employeeId) {
		Pager<EventList> pager = new Pager<EventList>();
		pager = this.eventListService.report(pager, eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId, mapName, buildName, siteName, operatorId, status, employeeId);
		pager.setData("event",  convertEventList(pager.getResults()));
		pager.setResults(null);
		
		return pager;
	}
	
	private List<EventListVo> convertEventList(List<EventList> list){
		List<EventListVo> returnList  = new ArrayList<EventListVo>();
		for(EventList obj : list) {
			EventListVo eventListVo = new EventListVo();
			BeanUtils.copyProperties(obj, eventListVo);
			setNode(obj,eventListVo);
			if(StringUtils.hasText(obj.getOperator())) {
				AdminUser operatorUser = this.adminUserService.findById(obj.getOperator());
				if(operatorUser!=null) {
					AdminUserVo adminUserVo = new AdminUserVo();
					BeanUtils.copyProperties(operatorUser, adminUserVo);
					adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(operatorUser.getHeadPortrait()));
					eventListVo.setOperatorVo(adminUserVo);
				}
			}
			if(StringUtils.hasText(obj.getCurrentHandleUser())) {
				AdminUser currentHandleUser = this.adminUserService.findById(obj.getCurrentHandleUser());
				if(currentHandleUser!=null) {
					AdminUserVo adminUserVo = new AdminUserVo();
					BeanUtils.copyProperties(currentHandleUser, adminUserVo);
					adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(currentHandleUser.getHeadPortrait()));
					eventListVo.setCurrentHandleUserVo(adminUserVo);
				}
			}

			returnList.add(eventListVo);
		}
		setEventMessage(returnList);
		return returnList;
	}
	
	private void setEventMessage(List<EventListVo> list) {
		for(EventListVo eventListVo : list) {
			List<EventMessage> listEventMessage= this.eventMessageService.findEventMessage(eventListVo.getId());
			eventListVo.setEventMessage(listEventMessage);
		}
	}
	
	private void setNode(EventList eventList,EventListVo eventListVo) {
		Node node = nodeService.findBySourceCode(StringUtils.hasText(eventList.getSourceCode())?eventList.getSourceCode():"111111111");
		if(node!=null) {
			NodeVo nodeVo = new NodeVo();
			BeanUtils.copyProperties(node, nodeVo);
			if(node.getStatus()!=null) {
				nodeVo.setStatusCode(node.getStatus().getName());
			}
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
			
			eventListVo.setNode(nodeVo);
		}
	}
	
}
