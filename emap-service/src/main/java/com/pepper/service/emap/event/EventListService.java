package com.pepper.service.emap.event;

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
	public Pager<EventList> List(Pager<EventList> pager, Integer warningLevel,Boolean isUrgent,String id);
	
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
}
