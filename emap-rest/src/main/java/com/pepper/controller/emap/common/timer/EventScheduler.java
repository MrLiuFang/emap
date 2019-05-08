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
	

//	@Scheduled(fixedRate = 5000)
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
					}else {
						eventList.setStatus("A");
						updateEventListStatus(eventList);
					}
				}
			}
		}
	}
	
	private void eventRule(EventList eventList,EventRule eventRule) {
		if(eventRule.getWarningLevel()!=null && eventList.getWarningLevel()<eventRule.getWarningLevel()) {
			eventList.setStatus("A");
			updateEventListStatus(eventList);
			return;
		}
		
		if(eventList.getCreateDate()!= null && eventRule.getTimeOut() != null && (new Date().getTime() - eventList.getCreateDate().getTime())/1000>eventRule.getTimeOut()) {
			eventList.setStatus("A");
			updateEventListStatus(eventList);
			return;
		}
		
		try {
			Calendar now = Calendar.getInstance();
			String dataStr1 = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH) +" " + eventRule.getFromDateTime()+ ":00";
			now.add(Calendar.DAY_OF_MONTH, 1); 
			String dataStr2 = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH) +" " + eventRule.getToDateTime()+ ":00";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = sdf.parse(dataStr1);
			Date date2 = sdf.parse(dataStr2);
			Long time = new Date().getTime();
			if(date1.getTime() > time && time < date2.getTime()) {
				List<AdminUser> list = adminUserService.findDepartmentManager(eventRule.getDepartmentId());
				EventDispatch eventDispatch = new EventDispatch();
				for(AdminUser user : list) {
					String deviceId = valueOperationsService.get("userDeviceId_"+user.getId());
					messageService.send(deviceId, "您有新的工单",eventList.getEventName());
					eventList.setCurrentHandleUser(user.getId());
					eventDispatch.setOperator(user.getId());
				}
				eventList.setStatus("W");
				eventList.setOperator("000000000000");
				eventList.setContent("系统自动派单");
				updateEventListStatus(eventList);	
				
				
				eventDispatch.setEventId(eventList.getEventId());
				eventDispatch.setDispatchFrom("000000000000");
				eventDispatch.setTitle(eventList.getEventName());
				eventDispatchService.save(eventDispatch);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		eventRule.getFromDateTime();
//		if()
	}
	
	public static void main(String agrs[]) {
		Calendar now = Calendar.getInstance();
		String data1 = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH) +" " + "18:00"+ ":00";
		now.add(Calendar.DAY_OF_MONTH, 1); 
		String data2 = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH) +" " + "09:00"+ ":00";
		System.out.println(data1);
		System.out.println(data2);
	}
	
	private Node getNode(String sourceCode) {
		return nodeService.findBySourceCode(sourceCode);
	}
	
	private void updateEventListStatus(EventList eventList) {
		eventListService.update(eventList);
	}
}
