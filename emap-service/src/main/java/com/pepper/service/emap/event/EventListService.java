package com.pepper.service.emap.event;

import java.util.Date;
import java.util.List;

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
	public Pager<EventList> transferList(Pager<EventList> pager,String dispatchFrom);
	
	/**
	 *  查询打卡记录
	 * @param pager
	 * @param eventListId
	 * @param startDate
	 * @param endDate
	 * @param staffId
	 * @return
	 */
	public Pager<EventList> doorAttendance(Pager<EventList> pager,String eventListId,Date startDate,Date endDate,String staffId);
	
	/**
	 * 获取我被请求协助的事件
	 * @param pager
	 * @param userId
	 * @return
	 */
	public Pager<EventList> assistEventList(Pager<EventList> pager,String userId);
}
