package com.pepper.dao.emap.event;

import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.event.ActionList;

public interface ActionListDao extends BaseDao<ActionList> {


	public List<ActionList> findFirstByEventId(String eventId); 
	
//	public ActionList findFirstByEventListAssistId(String eventListAssistId); 
	
	public List<ActionList> findByEventListId(String eventListId); 
	
	public ActionList findOneByEventListIdOrderByCreateDateDesc(String eventListId);
}
