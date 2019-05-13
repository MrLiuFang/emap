package com.pepper.service.emap.event;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.event.ActionList;

public interface ActionListService extends BaseService<ActionList> {

	public ActionList findByEventId(String eventId); 
	
	public ActionList findByActionListId(String actionListId); 
}
