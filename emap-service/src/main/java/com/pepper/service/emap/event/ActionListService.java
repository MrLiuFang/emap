package com.pepper.service.emap.event;

import java.util.List;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.event.ActionList;

public interface ActionListService extends BaseService<ActionList> {

	public List<ActionList> findByEventId(String eventId); 
	
//	public ActionList findActionList(String eventListAssistId);
	
	public List<ActionList> findByEventListId(String eventListId); 
}
