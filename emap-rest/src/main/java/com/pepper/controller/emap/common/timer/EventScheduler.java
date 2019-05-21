package com.pepper.controller.emap.common.timer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.event.EventDispatch;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.node.Node;
import com.pepper.service.console.admin.user.AdminUserService;
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
	

	@Scheduled(fixedRate = 5000)
	public void scheduled() {
		List<EventList> list = eventListService.findByStatusOrStatus(null, "");
		for(EventList eventList : list) {
			String sourceCode = eventList.getSourceCode();
			if(StringUtils.hasText(sourceCode)) {
				Node node = getNode(sourceCode);
				if(node != null) {
					EventRule eventRule = eventRuleService.findByNodeId(node.getId());
					if(eventRule != null) {
						eventRule(eventList,eventRule);
					}
				}
			}
		}
	}
	
	private void eventRule(EventList eventList,EventRule eventRule) {		
		try {
			Calendar now = Calendar.getInstance();
			Integer from =  Integer.valueOf( eventRule.getFromDateTime().replaceFirst("^0*", "").replace(":", ""));
			Integer to =  Integer.valueOf( eventRule.getToDateTime().replaceFirst("^0*", "").replace(":", ""));
//			Integer  time = now.get(Calendar.HOUR_OF_DAY);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
			Integer  time = Integer.valueOf(simpleDateFormat.format(new Date()).replaceFirst("^0*", "").replace(":", ""));
			if((time>=from&&time<=2359)||(time<=to&&time>=1)) {
				assignment(eventRule,eventList);
			}else {
				if(eventRule.getWarningLevel()!=null && eventList.getWarningLevel()>=eventRule.getWarningLevel()) {
					eventList.setStatus("A");
					updateEventListStatus(eventList);
					assignment(eventRule,eventList);
					return ;
				}else {
					if(eventList.getCreateDate()!= null && eventRule.getTimeOut() != null && (new Date().getTime() - eventList.getCreateDate().getTime())/1000>eventRule.getTimeOut()) {
						eventList.setStatus("A");
						updateEventListStatus(eventList);
						return ;
					}
				}
			}
		}catch (Exception e) {
			//eventList.setStatus("A");
			//updateEventListStatus(eventList);
			e.printStackTrace();
		}
	}
	
	private void assignment(EventRule eventRule,EventList eventList) {
		List<AdminUser> list = adminUserService.findDepartmentManager(eventRule.getDepartmentId());
		EventDispatch eventDispatch = new EventDispatch();
		for(AdminUser user : list) {
			String deviceId = valueOperationsService.get("userDeviceId_"+user.getId());
			messageService.send(deviceId, "您有新的工单",eventList.getEventName(),eventList.getId());
			eventList.setCurrentHandleUser(user.getId());
			eventDispatch.setOperator(user.getId());
		}
		eventList.setStatus("A");
		eventList.setContent("系統自動派單");
		if(!StringUtils.hasText(eventList.getCurrentHandleUser())) {
			return ;
		}
		updateEventListStatus(eventList);	
		
		eventDispatch.setCreateDate(new Date());
		eventDispatch.setEventId(eventList.getEventId());
		eventDispatch.setEventListId(eventList.getId());
		eventDispatch.setDispatchFrom("000000000000");
		eventDispatch.setTitle(eventList.getEventName());
		eventDispatchService.save(eventDispatch);
	}
	
	public static void main(String agrs[]) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		String test = simpleDateFormat.format(new Date()).replaceFirst("^0*", "").replace(":", "");
		System.out.println(test);
//		Calendar now = Calendar.getInstance();
//		String data1 = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH) +" " + "18:00"+ ":00";
//		now.add(Calendar.DAY_OF_MONTH, 1); 
//		String data2 = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH) +" " + "09:00"+ ":00";
//		System.out.println(data1);
//		System.out.println(data2);
	}
	
	private Node getNode(String sourceCode) {
		return nodeService.findBySourceCode(sourceCode);
	}
	
	private void updateEventListStatus(EventList eventList) {
		eventListService.update(eventList);
	}
}
