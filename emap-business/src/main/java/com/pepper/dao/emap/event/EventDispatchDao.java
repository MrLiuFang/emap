package com.pepper.dao.emap.event;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.event.EventDispatch;

public interface EventDispatchDao extends BaseDao<EventDispatch> {

	public EventDispatch findFirstByEventListIdAndOperatorOrderByCreateDateDesc(String eventId,String operator);
	
}
