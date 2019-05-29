package com.pepper.dao.emap.event;

import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.event.ActionList;

public interface ActionListDao extends BaseDao<ActionList> {


	public ActionList findByEventId(String eventId); 
	
	public List<ActionList> findByEventListId(String eventListId); 
}
