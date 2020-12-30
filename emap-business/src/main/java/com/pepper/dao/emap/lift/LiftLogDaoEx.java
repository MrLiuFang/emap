package com.pepper.dao.emap.lift;

import com.pepper.core.Pager;
import com.pepper.model.emap.lift.LiftLog;

import java.util.Date;

public interface LiftLogDaoEx {
    public Pager<LiftLog> List(Pager<LiftLog> pager, String liftId, Date startDate, Date endDate);
}
