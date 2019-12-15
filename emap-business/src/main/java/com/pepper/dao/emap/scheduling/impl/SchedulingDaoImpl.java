package com.pepper.dao.emap.scheduling.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseDao;
import com.pepper.dao.emap.scheduling.SchedulingDaoEx;
import com.pepper.model.emap.scheduling.Scheduling;

public class SchedulingDaoImpl implements SchedulingDaoEx {

	@Autowired
	private BaseDao<Scheduling> baseDao;
	
	@Override
	public Pager<Scheduling> findNavigator(Pager<Scheduling> pager, String userId, String userName, String departmentId,
			String departmentName, String departmentGroupId, String departmentGroupName, Date startDate, Date endDate) {
		StringBuffer jpql = new StringBuffer();
		jpql.append("select t1 from Scheduling t1 join AdminUser t2 on t1.userId = t2.id join Department t3 on t1.departmentId = t3.id join DepartmentGroup t4 on t1.departmentGroupId = t4.id where 1=1 ");
		java.util.Map<String,Object> searchParameter = new HashMap<String, Object>();
		if(StringUtils.hasText(userId)) {
			jpql.append(" and t2.id = :userId ");
			searchParameter.put(userId, userId);
		}
		if(StringUtils.hasText(userName)) {
			jpql.append(" and t2.name like :userName ");
			searchParameter.put("userName", "%"+userName+"%");
		}
		if(StringUtils.hasText(departmentName)) {
			jpql.append(" and t3.name like :departmentName ");
			searchParameter.put("departmentName", "%"+departmentName+"%");
		}
		if(StringUtils.hasText(departmentId)) {
			jpql.append(" and t3.id = :departmentId ");
			searchParameter.put("departmentId", departmentId);
		}
		if(StringUtils.hasText(departmentGroupName)) {
			jpql.append(" and t4.name like :departmentGroupName ");
			searchParameter.put("departmentGroupName", "%"+departmentGroupName+"%");
		}
		if(StringUtils.hasText(departmentGroupId)) {
			jpql.append(" and t4.id = :departmentGroupId ");
			searchParameter.put("departmentGroupId", departmentGroupId);
		}
		if(Objects.nonNull(startDate)) {
			jpql.append(" and t1.date >= :startDate ");
			searchParameter.put("startDate", startDate);
		}
		if(Objects.nonNull(endDate)) {
			jpql.append(" and t1.date <= :endDate ");
			searchParameter.put("endDate", endDate);
		}
		
		jpql.append(" order by t1.userId,t1.date,t1.departmentId,t1.departmentGroupId");
		return this.baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	}

}
