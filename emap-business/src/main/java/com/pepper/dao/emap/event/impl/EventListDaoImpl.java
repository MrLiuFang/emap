package com.pepper.dao.emap.event.impl;

import java.util.HashMap;
import java.util.Map;

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
	public Pager<EventList> List(Pager<EventList> pager, Integer warningLevel,Boolean isUrgent,String id) {
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		jpql.append(" from EventList where id in ( ");
		jpql.append("  select el.id from  EventList el where el.status = 'N'   AND el.warningLevel  " ).append(isUrgent?">=":"<");
		jpql.append(" (select  er.warningLevel from EventRule er join Node n on er.nodeId=n.id where el.sourceCode=n.sourceCode) ) ");
		jpql.append( " or id in ( ");
		jpql.append(" select el.id from  EventList el where el.status = 'N'   AND el.warningLevel  " ).append(isUrgent?">=":"<");
		jpql.append(" (select  er.warningLevel from EventRule er join Node n on n.nodeTypeId = er.nodeTypeId where el.sourceCode=n.sourceCode) ) ");
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



}
