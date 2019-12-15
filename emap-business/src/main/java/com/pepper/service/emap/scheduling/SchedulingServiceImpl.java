package com.pepper.service.emap.scheduling;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.Pager;
import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.scheduling.SchedulingDao;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.scheduling.Scheduling;

@Service(interfaceClass = SchedulingService.class)
public class SchedulingServiceImpl extends BaseServiceImpl<Scheduling> implements SchedulingService {

	@Resource
	private SchedulingDao schedulingDao;

	@Override
	public Boolean deleteByUserIdAndDate(String userId, Date date) {
		return schedulingDao.deleteByUserIdAndDate(userId, date)>0;
	}

	@Override
	public Integer deleteByUserIdAndDateBetween(String userId, Date startDate, Date endDate) {
		return schedulingDao.deleteByUserIdAndDateBetween(userId,startDate,endDate);
	}

	@Override
	public List<Scheduling> findByDate(Date date) {
		return schedulingDao.findByDate(date);
	}

	@Override
	public Pager<Scheduling> findNavigator(Pager<Scheduling> pager, String userId, String userName, String departmentId,
			String departmentName, String departmentGroupId, String departmentGroupName, Date startDate, Date endDate) {
		return schedulingDao.findNavigator(pager, userId, userName, departmentId, departmentName, departmentGroupId, departmentGroupName, startDate, endDate);
	}
}
