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
	public List<EventListAssist> findByEventListIdAndUserId(String eventListId,String userId);
	
	/**
	 * 
	 * @param eventListId
	 * @param userId
	 * @param employeeId
	 * @return
	 */
	public EventListAssist findFirstByEventListIdAndUserIdAndEmployeeId(String eventListId,String userId,String employeeId);
	
	public EventListAssist findFirstByEventListIdAndUserIdAndEmployeeIdAndIsFinish(String eventListId,String userId,String employeeId,Boolean isFinish);
	/**
	 * 
	 * @param eventListId
	 * @return
	 */
	public List<EventListAssist> findByEventListId(String eventListId);
	
	public List<EventListAssist> findByEventListIdAndIsFinish(String eventListId,Boolean isFinish);
	
	public List<EventListAssist> findByActionListId(String actionListId);
	
	public List<EventListAssist> findByEventListIdAndEmployeeId(String eventListId,String employeeId);
	
	public void deleteByEventListIdAndEmployeeIdAndIsFinish(String eventListId,String employeeId,Boolean isFinish);
}
