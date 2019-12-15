package com.pepper.dao.emap.event;

import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.event.EventDispatch;

public interface EventDispatchDao extends BaseDao<EventDispatch> {

	public EventDispatch findFirstByEventListIdAndOperatorOrderByCreateDateDesc(String eventId,String operator);
	
	public EventDispatch findFirstByEventListIdAndOperatorAndIsEmployeeTransferOrderByCreateDateDesc(String eventId,String operator,Boolean isEmployeeTransfer);
	
	public List<EventDispatch> findByEventListIdOrderByCreateDateDesc(String eventListId);
}
