package com.pepper.dao.emap.event;

import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.event.EventMessage;

public interface EventMessageDao extends BaseDao<EventMessage> {
	
	public List<EventMessage> findByEventListIdOrderByCreateDateDesc(String eventListId);

}	
