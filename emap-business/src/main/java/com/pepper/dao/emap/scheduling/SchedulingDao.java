package com.pepper.dao.emap.scheduling;

import java.util.Date;
import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.scheduling.Scheduling;

public interface SchedulingDao extends BaseDao<Scheduling> ,SchedulingDaoEx {

	public Integer deleteByUserIdAndDate(String userId,Date date);

	public Integer deleteByUserIdAndDateBetween(String userId,Date startDate,Date endDate);

	public List<Scheduling> findByDate( Date date);
}
