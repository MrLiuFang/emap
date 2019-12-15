package com.pepper.service.emap.scheduling;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseService;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.scheduling.Scheduling;

public interface SchedulingService extends BaseService<Scheduling>  {

	@Transactional
	public Boolean deleteByUserIdAndDate(String userId,Date date);

	@Transactional
	public Integer deleteByUserIdAndDateBetween(String userId,Date startDate,Date endDate);

	public List<Scheduling> findByDate(Date date) ;

	public Pager<Scheduling> findNavigator(Pager<Scheduling> pager, String userId, String userName, String departmentId,
			String departmentName, String departmentGroupId, String departmentGroupName, Date startDate, Date endDate);
}
