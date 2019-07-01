package com.pepper.service.emap.event;

import java.util.List;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.event.EventListAssist;

public interface EventListAssistService extends BaseService< EventListAssist>{

	public EventListAssist findEventListAssist(String eventListId,String userId,Boolean isFinish);
	
	public EventListAssist findEventListAssist(String eventListId,String userId);
	
	public EventListAssist findEventListAssist(String eventListId,String userId,String employeeId);
	public EventListAssist findEventListAssist(String eventListId, String userId,
			String employeeId,Boolean isFinish);
	
	/**
	 * 
	 * @param eventListId
	 * @return
	 */
	public List<EventListAssist> findEventListAssist(String eventListId);
	
	public List<EventListAssist> findByActionListId(String actionListId);
	
	public List<EventListAssist> findEventListAssist(String eventListId,Boolean isFinish);
	
	public List<EventListAssist> findByEventListIdAndEmployeeId(String eventListId, String employeeId);
	
}
