package com.pepper.dao.emap.event;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.event.EventList;

public interface EventListDao extends BaseDao<EventList>, EventListDaoEx {

	/**
	 * 查询未处理的事件
	 * @param status
	 * @param status1
	 * @return
	 */
	public List<EventList> findByStatusOrStatus(String status,String status1);
	
	/**
	 * 查询历史事件
	 * @param sourceCode
	 * @param id
	 * @return
	 */
	public Page<EventList> findBySourceCodeAndIdNot(String sourceCode,String id,Pageable pageable);
	
	/**
	 * 交接工作
	 * @param handoverUserId
	 * @param currentUserId
	 */
	@Modifying
	@Query(" update EventList set operator = ?1 where  operator=?2 and status <> 'P' ")
	public void handover(String handoverUserId, String currentUserId);
	

}
