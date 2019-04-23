package com.pepper.dao.emap.event.impl;

import java.util.HashMap;
import java.util.Map;

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
	public Pager<EventList> List(Pager<EventList> pager, Integer warningLevel,Boolean isUrgent) {
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		jpql.append(" from  EventList where (status = '' or status is null)  ");
		if(warningLevel !=null && warningLevel>0) {
			if(isUrgent!= null && isUrgent) {
				jpql.append(" and warningLevel >= :warningLevel ");
				searchParameter.put("warningLevel", warningLevel);
			}else if(isUrgent!= null && !isUrgent) { 
				jpql.append(" and warningLevel < :warningLevel ");
				searchParameter.put("warningLevel", warningLevel);
			}
		}
		BaseDao<EventList> baseDao =  this.getPepperSimpleJpaRepository(this.getClass());
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	}



}
