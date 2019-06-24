package com.pepper.dao.emap.event.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseDao;
import com.pepper.core.base.curd.DaoExImpl;
import com.pepper.dao.emap.event.EventListDaoEx;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.HelpList;

/**
 * 
 * @author Mr.Liu
 *
 */
public class EventListDaoImpl  extends DaoExImpl<EventList> implements EventListDaoEx {

	@Override
	public Pager<EventList> List(Pager<EventList> pager, Boolean isUrgent) {
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		jpql.append(" from EventList where id in ( ");
		jpql.append("  select el.id from  EventList el where el.status = 'N'   AND el.warningLevel  " ).append(isUrgent?">=":"<");
		jpql.append(" (select  er.warningLevel from EventRule er join Node n on er.nodeId=n.id where el.sourceCode=n.sourceCode) ) ");
		jpql.append( " or id in ( ");
		jpql.append(" select el.id from  EventList el where el.status = 'N'   AND el.warningLevel  " ).append(isUrgent?">=":"<");
		jpql.append(" (select  er.warningLevel from EventRule er join Node n on n.nodeTypeId = er.nodeTypeId where el.sourceCode=n.sourceCode) ) order by createDate desc  ");
		BaseDao<EventList> baseDao =  this.getPepperSimpleJpaRepository(this.getClass());
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
		
	}

	@Override
	public Pager<EventList> transferList(Pager<EventList> pager,String dispatchFrom) {
		BaseDao<EventList> baseDao =  this.getPepperSimpleJpaRepository(this.getClass());
		StringBuffer jpql = new StringBuffer();
		jpql.append("select  distinct el from  EventList el join EventDispatch ed on el.id = ed.eventListId where ed.dispatchFrom = :dispatchFrom ");
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		searchParameter.put("dispatchFrom", dispatchFrom);
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	}


	public Pager<EventList> doorAttendance(Pager<EventList> pager,String eventListId,Date startDate,Date endDate,String staffId){
		BaseDao<EventList> baseDao =  this.getPepperSimpleJpaRepository(this.getClass());
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		
		jpql.append(" select el from EventList el  ");
		if(StringUtils.hasText(staffId)) {
			jpql.append(" join Staff s on el.idCard = s.idCard  ");
		}
		
		jpql.append(" where 1=1 ");
		
		if(StringUtils.hasText(eventListId)) {
			Optional<EventList> optional = baseDao.findById(eventListId);
			EventList eventList = optional.isPresent()?optional.get():null;
			if(eventList!=null) {
				jpql.append(" and el.sourceCode =:sourceCode ");
				searchParameter.put("sourceCode", eventList.getSourceCode());
			}
		}
		
		if(startDate!=null) {
			jpql.append(" and el.createDate >=:startDate ");
			searchParameter.put("startDate",startDate);
		}
		
		if(endDate!=null) {
			jpql.append(" and el.createDate <=:endDate ");
			searchParameter.put("endDate",endDate);
		}
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	}
	
	public Pager<EventList> assistEventList(Pager<EventList> pager,String userId){
		BaseDao<EventList> baseDao =  this.getPepperSimpleJpaRepository(this.getClass());
		StringBuffer jpql = new StringBuffer();
		jpql.append(" select el from EventList el join EventListAssist ea on el.id = ea.eventListId where ea.userId = :userId order by ea.isFinish asc,ea.createDate desc ");
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		searchParameter.put("userId", userId);
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	} 

}
