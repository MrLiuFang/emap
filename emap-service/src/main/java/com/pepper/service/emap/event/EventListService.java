package com.pepper.service.emap.event;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pepper.model.emap.node.Node;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseService;
import com.pepper.model.emap.event.EventList;

public interface EventListService extends BaseService<EventList> {

	/**
	 *  查询未处理的事件
	 * @param status
	 * @param status1
	 * @return
	 */
	public List<EventList> findByStatusOrStatus(String status,String status1);

	public List<EventList> findByStatusNot(String status);
	/**
	 *
	 * @param pager
	 * @param warningLevel
	 * @return
	 */
	public Pager<EventList> List(Pager<EventList> pager, Boolean isUrgent);

	/**
	 * 查询历史事件
	 * @param sourceCode
	 * @param id
	 * @return
	 */
	public Pager<EventList> findBySourceCodeAndIdNot(String sourceCode, String id,Pager<EventList> pager) ;


	/**
	 *
	 * @param handoverUserId
	 * @param currentUserId
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void handover(String handoverUserId,String currentUserId);


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

	public Pager<EventList>  historyEventList(Pager<EventList> pager,String event,Integer warningLevel,String node,String nodeType,String mapName,String buildName,String stieName,String operator,String status,String eventId,Date startDate,Date endDate,String departmentId);

	public Pager<EventList>  report(Pager<EventList> pager,Date eventStartDate, Date eventEndDate, String event,Integer warningLevel,String node,String nodeTypeId,String mapName,String buildName,String siteName,String operatorId,String status ,String employeeId,Boolean isOrder,String sortBy ,Boolean isSpecial,Boolean isUrgent);

	public EventList findOneByNodeId(String nodeId);

	Pager<EventList> appList(Pager<EventList> pager, Boolean isFinish, Boolean isUrgent, String eventId, String nodeName,
							 String eventName, Date startDate, Date endDate,String currentHandleUser);

	public Integer toMeNoFiledCount(String userId);

	public Integer todaySpecialCount(Date startDate,Date endDate);

	public Integer todayUrgentCount(Date startDate,Date endDate);

	public Integer todayOrdinaryCount(Date startDate,Date endDate);

	public List<Map<String,Object>> currentMonthCount(Date startDate, Date endDate);

	public List<Map<String,Object>> currentMonthCount(Date startDate, Date endDate,Boolean isConsole);

	public List<Map<String,Object>> currentMonthNotRelationNode(Date startDate,Date endDate);
	/**
	 *
	 * @param where
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Map<String,Object>> yearTypeCount(String where, Date startDate, Date endDate);

	@Transactional(propagation=Propagation.REQUIRED)
	public int delete(Date createDate);

	public void otherTreatment(EventList eventList);

	public void sendTcp(Node node,Boolean outIsOn,String nodeGroupCode) throws InterruptedException;

	public void send(Node node,String cmd) throws InterruptedException;
	void updateStatus(String id);

	EventList findFirstBySourceCodeAndStatusNot(String sourceCode,String status);

	public void filed(String id,String sourceCode);
}