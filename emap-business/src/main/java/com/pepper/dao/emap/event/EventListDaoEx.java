package com.pepper.dao.emap.event;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pepper.core.Pager;
import com.pepper.model.emap.event.EventList;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface EventListDaoEx {
	
	/**
	 * 
	 * @param pager
	 * @param warningLevel
	 * @return
	 */
	public Pager<EventList> List(Pager<EventList> pager, Boolean isUrgen);
	
	/**
	 * 获取我已转移的事件
	 * @param pager
	 * @return
	 */
	public Pager<EventList> transferList(Pager<EventList> pager,String dispatchFrom,String eventId,Boolean isUrgent,String nodeName,String eventName,Date startDate,Date endDate);
	
	/**
	 *  查询打卡记录
	 * @param pager
	 * @param eventListId
	 * @param startDate
	 * @param endDate
	 * @param staffId
	 * @return
	 */
	public Pager<EventList> doorAttendance(Pager<EventList> pager,String eventListId,String nodeId,Date startDate,Date endDate,String staffId);
	
	/**
	 * 获取我被请求协助的事件
	 * @param pager
	 * @param userId
	 * @return
	 */
	public Pager<EventList> assistEventList(Pager<EventList> pager,String userId,Boolean isFinish);
	
	public Pager<EventList>  historyEventList(Pager<EventList> pager,Date eventStartDate, Date eventEndDate, String event,Integer startWarningLevel, Integer endWarningLevel,String node,String nodeType,String mapName,String buildName,String siteName,String operatorId,String status , String employeeId,Boolean isOrder,String sortBy,Boolean isSpecial,Boolean isUrgent,Boolean isRoutine,String eventId, String departmentId);
	
	public EventList findFirstByNodeId(String nodeId);

	Pager<EventList> appList(Pager<EventList> pager, Boolean isFinish, Boolean isUrgent, String eventId, String nodeName,
			String eventName, Date startDate, Date endDate,String currentHandleUser);

	/**
	 *
	 * @param where
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Map<String,Object>> yearTypeCount(String where, Date startDate, Date endDate);

	public List<String> userNode(String userId);
}
