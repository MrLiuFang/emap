package com.pepper.controller.emap.common.timer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.department.DepartmentGroup;
import com.pepper.model.emap.event.EventDispatch;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.node.Node;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.emap.department.DepartmentGroupService;
import com.pepper.service.emap.event.EventDispatchService;
import com.pepper.service.emap.event.EventListService;
import com.pepper.service.emap.event.EventRuleService;
import com.pepper.service.emap.message.MessageService;
import com.pepper.service.emap.node.NodeService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;

@Component
public class EventScheduler {

	@Reference
	private EventListService  eventListService;
	
	@Reference
	private NodeService nodeService;
	
	@Reference
	private EventRuleService eventRuleService;
	
	@Reference
	private MessageService messageService;
	
	@Reference
	private AdminUserService adminUserService;
	
	@Reference
	private ValueOperationsService valueOperationsService;
	
	@Reference
	private EventDispatchService eventDispatchService;
	
	@Reference
	private DepartmentGroupService departmentGroupService;
	

	@Scheduled(fixedRate = 5000)
	public void scheduled() {
		List<EventList> list = eventListService.findByStatusOrStatus(null, "N");
		for(EventList eventList : list) {
			String sourceCode = eventList.getSourceCode();
			if(!StringUtils.hasText(eventList.getStatus())) {
				eventList.setStatus("N");
				eventListService.update(eventList);
			}
			if(StringUtils.hasText(sourceCode)) {
				Node node = getNode(sourceCode);
				if(node != null) {
					EventRule eventRule = eventRuleService.findByNodeId(node.getId());
					if(eventRule != null) {
						eventRule(eventList,eventRule);
					}else {
						eventRule = this.eventRuleService.findByNodeTypeId(node.getNodeTypeId());
						if(eventRule!=null) {
							eventRule(eventList,eventRule);
						}
					}
				}
			}
		}
	}
	
	private void eventRule(EventList eventList,EventRule eventRule) {		
		try {
			if(!StringUtils.hasText(eventRule.getFromDateTime())||!StringUtils.hasText(eventRule.getToDateTime())) {
				return;
			}
			Integer from =  Integer.valueOf( eventRule.getFromDateTime().replaceFirst("^0*", "").replace(":", ""));
			Integer to =  Integer.valueOf( eventRule.getToDateTime().replaceFirst("^0*", "").replace(":", ""));
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
			Integer  time = Integer.valueOf(simpleDateFormat.format(new Date()).replaceFirst("^0*", "").replace(":", ""));
			if((time>=from&&time<=2359)||(time<=to&&time>=1)) {
				assignment(eventRule,eventList);
			}else {
				if(eventRule.getWarningLevel()!=null && eventList.getWarningLevel()>=eventRule.getWarningLevel()) {
					assignment(eventRule,eventList);
					return ;
				}else {
					if(eventList.getCreateDate()!= null && eventRule.getTimeOut() != null && (new Date().getTime() - eventList.getCreateDate().getTime())/1000>eventRule.getTimeOut()) {
						assignment(eventRule,eventList);
						return ;
					}
				}
			}
		}catch (Exception e) {
		}
	}
	
	private Boolean assignment(EventRule eventRule,EventList eventList) {
		List<DepartmentGroup> listDepartmentGroup = this.departmentGroupService.findByDepartmentId(eventRule.getDepartmentId());
		AdminUser user = null;
		for(DepartmentGroup departmentGroup : listDepartmentGroup) {
			if(StringUtils.hasText(departmentGroup.getStartTime())&&StringUtils.hasText(departmentGroup.getEndTime())) {
				Integer startTime =  Integer.valueOf( departmentGroup.getStartTime().replaceFirst("^0*", "").replace(":", ""));
				Integer endTime =  Integer.valueOf( departmentGroup.getEndTime().replaceFirst("^0*", "").replace(":", ""));
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
				Integer  time = Integer.valueOf(simpleDateFormat.format(new Date()).replaceFirst("^0*", "").replace(":", ""));
				if(endTime<=startTime) {
					if((time>=startTime&&time<=2359)||(time<=endTime&&time>=1)) {
						user=this.getCurrentHandleUser(eventList, departmentGroup);
						break;
					}
				}else {
					if(time>=startTime&&time<=endTime) {
						user=this.getCurrentHandleUser(eventList, departmentGroup);
						break;
					}
				}
			}
		}
		if(user== null) {
			return false;
		}	
		String deviceId = valueOperationsService.get("userDeviceId_"+user.getId());
		messageService.send(deviceId, "您有新的工单",eventList.getEventName(),eventList.getId());
		eventList.setCurrentHandleUser(user.getId());
		eventList.setStatus("A");
		eventList.setAssignDate(new Date());
		eventList.setContent("系統自動派單");
		if(!StringUtils.hasText(eventList.getCurrentHandleUser())) {
			return false;
		}
		updateEventListStatus(eventList);
		
		EventDispatch eventDispatch = new EventDispatch();
		eventDispatch.setOperator(user.getId());
		eventDispatch.setCreateDate(new Date());
		eventDispatch.setEventId(eventList.getEventId());
		eventDispatch.setEventListId(eventList.getId());
		eventDispatch.setDispatchFrom("000000000000");
		eventDispatch.setTitle(eventList.getEventName());
		eventDispatchService.save(eventDispatch);
		return true;
	}
	
	private AdminUser getCurrentHandleUser(EventList eventList,DepartmentGroup departmentGroup) {
		List<AdminUser> list = adminUserService.findByDepartmentGroupId(departmentGroup.getId(), true);
		for(AdminUser user : list) {
			return user;
		}
		return null;
	}
	
	public static void main(String agrs[]) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		String test = simpleDateFormat.format(new Date()).replaceFirst("^0*", "").replace(":", "");
		System.out.println(test);
	}
	
	private Node getNode(String sourceCode) {
		return nodeService.findBySourceCode(sourceCode);
	}
	
	private void updateEventListStatus(EventList eventList) {
		eventListService.update(eventList);
	}
}
