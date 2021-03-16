package com.pepper.controller.emap.common.timer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.xmlbeans.impl.inst2xsd.VenetianBlindStrategy;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pepper.controller.emap.util.Internationalization;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.department.DepartmentGroup;
import com.pepper.model.emap.event.EventDispatch;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventMessage;
import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.event.HelpList;
import com.pepper.model.emap.node.Node;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.emap.department.DepartmentGroupService;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.service.emap.event.EventDispatchService;
import com.pepper.service.emap.event.EventListService;
import com.pepper.service.emap.event.EventMessageService;
import com.pepper.service.emap.event.EventRuleService;
import com.pepper.service.emap.event.HelpListService;
import com.pepper.service.emap.message.MessageService;
import com.pepper.service.emap.node.NodeService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;

@Component
@Order(value=Ordered.LOWEST_PRECEDENCE)
public class EventScheduler {
	
	@Resource
	private Environment environment;

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
	
	@Reference
	private DepartmentService departmentService;
	
	@Reference
	private HelpListService helpListService;
	
	@Reference
	private EventMessageService eventMessageService;

	@Scheduled(fixedRate = 5000)
	public void scheduled() {
		if(!environment.getProperty("scheduler.enabled", "true").equals("true")) {
			return;
		}
		List<EventList> list = eventListService.findByStatusOrStatus(null, "W");
		for(EventList eventList : list) {
			eventList.setIsUrgent(eventList.getWarningLevel()>=getUrgentWarningLevel(eventList));
			String sourceCode = eventList.getSourceCode();
			if(!StringUtils.hasText(eventList.getStatus())) {
				eventList.setStatus("N");
				eventListService.update(eventList);
			}
			if(eventList.getWarningLevel()==0) {
				eventList.setStatus("P");
				eventList.setContent("自动归档");
				eventList.setOperator("000000000000");
				this.eventListService.update(eventList);
				continue ;
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
						}else {
							eventList.setIsNotFoundEventRule(true);
							eventList.setIsNotFoundEmployee(true);
							eventListService.update(eventList);
						}
					}
				}
			}
		}
	}
	private Integer getUrgentWarningLevel(EventList eventList) {
		Node node = this.nodeService.findBySourceCode(eventList.getSourceCode());
		EventRule eventRule = this.eventRuleService.findByNodeId(node==null?"0":node.getId());
		if(eventRule == null) {
			eventRule = eventRuleService.findByNodeTypeId(node==null?"0":node.getNodeTypeId());
		}
		return eventRule==null?0:eventRule.getWarningLevel();
	}
	
	private void eventRule(EventList eventList,EventRule eventRule) {		
		try {
			//特级
			if(eventRule.getSpecialWarningLevel()!=null&&eventRule.getSpecialWarningLevel()>0&&eventList.getWarningLevel()>=eventRule.getSpecialWarningLevel()) {
				if(StringUtils.hasText(eventRule.getSpecialDepartmentId())) {
//					AdminUser user = assignment(eventRule.getSpecialDepartmentId(),eventList,eventRule.getPushContent());
					eventList.setIsSpecial(true);
					eventListService.update(eventList);
//					send(eventList,eventRule,user);
					return ;
				}else {
					eventList.setIsNotFoundEmployee(true);
					eventListService.update(eventList);
					return ;
				}
			}
			eventList.setIsSpecial(false);
			eventListService.update(eventList);

			if(StringUtils.hasText(eventRule.getFromDateTime())&&StringUtils.hasText(eventRule.getToDateTime())) {
				Integer from =  Integer.valueOf( eventRule.getFromDateTime().replaceFirst("^0*", "").replace(":", ""));
				Integer to =  Integer.valueOf( eventRule.getToDateTime().replaceFirst("^0*", "").replace(":", ""));
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
				Integer  time = Integer.valueOf(simpleDateFormat.format(new Date()).replaceFirst("^0*", "").replace(":", ""));
				if(to<from) {
					if((time>=from&&time<=2359)||(time<=to&&time>=1)) {
//						AdminUser user = assignment(eventRule.getDepartmentId(),eventList,eventRule.getPushContent());
//						send(eventList,eventRule,user);
						return ;
					}
				}else {
					if(time>=from&&time<=to) {
//						AdminUser user = assignment(eventRule.getDepartmentId(),eventList,eventRule.getPushContent());
//						send(eventList,eventRule,user);
						return ;
					}
				}
			}
			//紧急
			if(eventRule.getWarningLevel()!=null && eventList.getWarningLevel()>=eventRule.getWarningLevel()) {
//				AdminUser user = assignment(eventRule.getDepartmentId(),eventList,eventRule.getPushContent());
//				send(eventList,eventRule,user);
				return ;
			}
			
			if(eventList.getCreateDate()!= null && eventRule.getTimeOut() != null && (new Date().getTime() - eventList.getCreateDate().getTime())/1000>eventRule.getTimeOut()) {
//				AdminUser user = assignment(eventRule.getDepartmentId(),eventList,eventRule.getPushContent());
//				send(eventList,eventRule,user);
				return ;
			}
			
			
			
		}catch (Exception e) {
//			e.printStackTrace();
		}
	}

	private void send(EventList eventList,EventRule eventRule,AdminUser user){
		EventMessage eventMessage = new EventMessage();
		eventMessage.setEventId(eventList.getEventId());
		eventMessage.setEventListId(eventList.getId());
		if(Objects.nonNull(user)) {
			eventMessage.setUserId(user.getId());
			eventMessage.setUserName(user.getName());
		}

		if(eventRule.getSendEmailWarningLevel()==1 && !eventList.getIsUrgent()){
			sendEmail(eventMessage,eventRule);
		}

		if(eventRule.getSendEmailWarningLevel()==2 && eventList.getIsUrgent() && !eventList.getIsSpecial()){
			sendEmail(eventMessage,eventRule);
		}

		if(eventRule.getSendEmailWarningLevel()==3 && eventList.getIsSpecial()){
			sendEmail(eventMessage,eventRule);
		}

		if(eventRule.getSendSmsWarningLevel()==1 && !eventList.getIsUrgent()){
			sendSms(eventMessage,eventRule,eventList);
		}

		if(eventRule.getSendSmsWarningLevel()==2 && eventList.getIsUrgent() && !eventList.getIsSpecial()){
			sendSms(eventMessage,eventRule,eventList);
		}

		if(eventRule.getSendSmsWarningLevel()==3 && eventList.getIsSpecial()){
			sendSms(eventMessage,eventRule,eventList);
		}

	}

	private void sendSms(EventMessage eventMessage,EventRule eventRule, EventList eventList){
		if(StringUtils.hasText(eventRule.getsMSReceiver())) {
			eventMessage.setType(2);
			Department department = this.departmentService.findById(eventRule.getSpecialDepartmentId());
			eventMessage.setMessage(eventList.getEventDate()+ "/" + (Objects.nonNull(department)?department.getName():"") + "/"+ eventList.getEventName() + "/"+ eventRule.getsMSContent());
			String[] mobile = eventRule.getsMSReceiver().split(";");
			for(String str : mobile) {
				eventMessage.setMobile(str);
				eventMessageService.save(eventMessage);
				eventMessage.setId(null);
			}
		}
	}

	private void sendEmail(EventMessage eventMessage,EventRule eventRule){
		if(StringUtils.hasText(eventRule.getEmailAccount())) {
			eventMessage.setType(1);
			eventMessage.setEmail(eventRule.getEmailAccount());
			eventMessage.setTitle(eventRule.getEmailTitle());
			eventMessage.setMessage(eventRule.getEmailContent());
			eventMessageService.save(eventMessage);
		}
	}
	
	private AdminUser assignment(String departmentId,EventList eventList,String pushTitle) {
		List<DepartmentGroup> listDepartmentGroup = this.departmentGroupService.findByDepartmentId(departmentId);
		AdminUser user = null;
		for(DepartmentGroup departmentGroup : listDepartmentGroup) {
			if(StringUtils.hasText(departmentGroup.getStartTime())&&StringUtils.hasText(departmentGroup.getEndTime())) {
				Integer startTime =  Integer.valueOf( departmentGroup.getStartTime().replaceFirst("^0*", "").replace(":", ""));
				Integer endTime =  Integer.valueOf( departmentGroup.getEndTime().replaceFirst("^0*", "").replace(":", ""));
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
				Integer  time = Integer.valueOf(simpleDateFormat.format(new Date()).replaceFirst("^0*", "").replace(":", ""));
				if(endTime<=startTime) {
					if((time>=startTime&&time<=2359)||(time<=endTime&&time>=1)) {
						user=this.getCurrentHandleUser( departmentGroup);
						break;
					}
				}else {
					if(time>=startTime&&time<=endTime) {
						user=this.getCurrentHandleUser( departmentGroup);
						break;
					}
				}
			}
		}
		if(user== null && Objects.nonNull(departmentId)) {
			List<AdminUser> list = this.adminUserService.findByDepartmentId(departmentId, true);
			user = getCurrentHandleUser(list);
		}	
		if(user== null) {
			eventList.setIsNotFoundEmployee(true);
			eventListService.update(eventList);
			return null;
		}
		eventList.setIsNotFoundEmployee(false);
		eventList.setCurrentHandleUser(user.getId());
		eventList.setStatus("A");
		eventList.setAssignDate(new Date());
		eventList.setContent("系統自動派單");
		Node node = nodeService.findBySourceCode(eventList.getSourceCode());
		if(node!=null) {
			List<HelpList> list = helpListService.findByNodeTypeId(node.getNodeTypeId());
			StringBuffer sb = new StringBuffer("[");
			for (int i = 0, len = list.size(); i < len; ++i) {
	            if (i > 0) {
	                sb.append(',');
	            }
	            sb.append("\""+list.get(i).getId()+"\"");
	        }
			sb.append("]");
			eventList.setHelpId(sb.toString());
		}
		if(!StringUtils.hasText(eventList.getCurrentHandleUser())) {
			return null;
		}
		eventListService.update(eventList);
		
		EventDispatch eventDispatch = new EventDispatch();
		eventDispatch.setDepartmentId(adminUserService.findById(eventList.getCurrentHandleUser()).getDepartmentId());
		eventDispatch.setOperator(user.getId());
		eventDispatch.setCreateDate(new Date());
		eventDispatch.setEventId(eventList.getEventId());
		eventDispatch.setEventListId(eventList.getId());
		eventDispatch.setDispatchFrom("000000000000");
		eventDispatch.setTitle(eventList.getEventName());
		eventDispatchService.save(eventDispatch);
		
		String deviceId = valueOperationsService.get("userDeviceId_"+user.getId());
		messageService.send(deviceId,StringUtils.hasText(pushTitle)?pushTitle: "您有新的工單",eventList.getEventName(),eventList.getId());
		
		EventMessage eventMessage = new EventMessage();
		eventMessage.setEventId(eventList.getEventId());
		eventMessage.setEventListId(eventList.getId());
		eventMessage.setUserId(user.getId());
		eventMessage.setUserName(user.getName());
		eventMessage.setType(3);
		eventMessage.setMessage(StringUtils.hasText(pushTitle)?pushTitle: "您有新的工單"+":"+eventList.getEventName());
		eventMessageService.save(eventMessage);
		
		return user;
	}
	
	private AdminUser getCurrentHandleUser(DepartmentGroup departmentGroup) {
		List<AdminUser> list = adminUserService.findByDepartmentGroupId(departmentGroup.getId(), true);
		return getCurrentHandleUser(list);
	}
	
	private AdminUser getCurrentHandleUser(List<AdminUser> list) {
		for(AdminUser user : list) {
			return user;
		}
		return null;
	}
	
	public static void main(String agrs[]) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		String test = simpleDateFormat.format(new Date()).replaceFirst("^0*", "").replace(":", "");
		System.out.println( Integer.valueOf("00:00".replaceFirst("^0*", "").replace(":", "")));
	}
	
	private Node getNode(String sourceCode) {
		return nodeService.findBySourceCode(sourceCode);
	}
	
}
