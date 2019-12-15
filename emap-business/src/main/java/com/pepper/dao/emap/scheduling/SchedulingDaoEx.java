package com.pepper.dao.emap.scheduling;

import java.util.Date;

import com.pepper.core.Pager;
import com.pepper.model.emap.scheduling.Scheduling;

public interface SchedulingDaoEx {
	public Pager<Scheduling> findNavigator(Pager<Scheduling> pager, String userId, String userName, String departmentId,
			String departmentName, String departmentGroupId, String departmentGroupName, Date startDate, Date endDate);
}
