package com.pepper.dao.emap.event;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

	public List<EventList> findByStatus(String status);

	public List<EventList> findByStatusNot(String status);

	/**
	 * 查询历史事件
	 * @param sourceCode
	 * @param id
	 * @return
	 */
	public Page<EventList> findBySourceCodeAndIdNotAndWarningLevelNot(String sourceCode,String id,Integer warningLevel ,Pageable pageable);

	public Page<EventList> findBySourceCodeAndIdNot(String sourceCode,String id,Pageable pageable);

	/**
	 * 交接工作
	 * @param handoverUserId
	 * @param currentUserId
	 */
	@Modifying
	@Query(" update EventList set operator = ?1 where  operator=?2 and status <> 'P' ")
	public void handover(String handoverUserId, String currentUserId);

	public Integer countByCurrentHandleUserAndStatusNot(String userId,String status);

	@Query("select  count(t1.id)  from EventList t1  where t1.isSpecial is true  and t1.createDate>= ?1 and t1.createDate<= ?2 ")
	public Integer todaySpecialCount(Date startDate,Date endDate);

	@Query("select  count(t1.id)  from EventList t1  where t1.isUrgent is true  and t1.createDate>= ?1 and t1.createDate<= ?2 ")
	public Integer todayUrgentCount(Date startDate,Date endDate);

	@Query("select  count(t1.id)  from EventList t1  where t1.isUrgent is not true and t1.isSpecial is not true   and t1.createDate>= ?1 and t1.createDate<= ?2 ")
	public Integer todayOrdinaryCount(Date startDate,Date endDate);

	@Query("select t4.name as name,  count(t1.id) as count from EventList t1 join Node t2 on t1.sourceCode = t2.sourceCode " +
			" join NodeType t3 on t2.nodeTypeId = t3.id join NodeClassify t4 on t3.nodeClassifyId = t4.id  where   t1.createDate>= ?1 and t1.createDate<= ?2 and t1.isConsole is null group by t4.name ")
	public List<Map<String,Object>> currentMonthCount(Date startDate,Date endDate);

	@Query("select '人工申報' as name,  count(t1.id) as count from EventList t1  where   t1.createDate>= ?1 and t1.createDate<= ?2 and t1.isConsole is true ")
	public List<Map<String,Object>> currentMonthIsConsoleCount(Date startDate,Date endDate);

	@Query("select 'others' as name,  count(t1.id) as count from EventList t1  where t1. sourceCode not in (select t2.sourceCode from Node t2 )  and   t1.createDate>= ?1 and t1.createDate<= ?2 and t1.isConsole is null ")
	public List<Map<String,Object>> currentMonthNotRelationNode(Date startDate,Date endDate);

	public Integer deleteByCreateDateLessThanEqual(Date createDate);
}
