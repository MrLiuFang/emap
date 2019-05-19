package com.pepper.dao.emap.event;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.event.ActionList;

public interface ActionListDao extends BaseDao<ActionList> {


	public ActionList findByEventId(String eventId); 
	
	public ActionList findByEventListId(String eventListId); 
}
