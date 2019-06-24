package com.pepper.service.emap.event;

import java.util.List;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.event.EventListAssist;

public interface EventListAssistService extends BaseService< EventListAssist>{

	public EventListAssist findEventListAssist(String eventListId,String userId,Boolean isFinish);
	
	public EventListAssist findEventListAssist(String eventListId,String userId);
	
	/**
	 * 
	 * @param eventListId
	 * @return
	 */
	public List<EventListAssist> findByeventListId(String eventListId);
}
