package com.pepper.dao.emap.event;

import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.event.EventListAssist;

public interface EventListAssistDao extends BaseDao<EventListAssist> {

	/**
	 * 
	 * @param eventListId
	 * @param userId
	 * @param isFinish
	 * @return
	 */
	public EventListAssist findFirstByEventListIdAndUserIdAndIsFinish(String eventListId,String userId,Boolean isFinish);
	
	/**
	 * 
	 * @param eventListId
	 * @param userId
	 * @return
	 */
	public EventListAssist findFirstByEventListIdAndUserId(String eventListId,String userId);
	
	/**
	 * 
	 * @param eventListId
	 * @return
	 */
	public List<EventListAssist> findByeventListId(String eventListId);
}
