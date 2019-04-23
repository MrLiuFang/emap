package com.pepper.controller.emap.common.timer;

import java.util.Date;
import java.util.List;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.node.Node;
import com.pepper.service.emap.event.EventListService;
import com.pepper.service.emap.event.EventRuleService;
import com.pepper.service.emap.node.NodeService;

@Component
public class EventScheduler {

	@Reference
	private EventListService  eventListService;
	
	@Reference
	private NodeService nodeService;
	
	@Reference
	private EventRuleService eventRuleService;
	

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
		if(eventList.getWarningLevel()<eventRule.getWarningLevel()) {
			eventList.setStatus("A");
			updateEventListStatus(eventList);
			return;
		}
		
		if((new Date().getTime() - eventList.getCreateDate().getTime())/1000>eventRule.getTimeOut()) {
			//TODO 通知APP处理
		}else {
			
		}
	}
	
//	private 
	
	private Node getNode(String sourceCode) {
		return nodeService.findBySourceCode(sourceCode);
	}
	
	private void updateEventListStatus(EventList eventList) {
		eventListService.update(eventList);
	}
}
