package com.pepper.service.emap.lift;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseService;
import com.pepper.model.emap.lift.LiftLog;

import java.util.Date;

public interface LiftLogService extends BaseService<LiftLog>  {
    public Pager<LiftLog> List(Pager<LiftLog> pager, String liftId, Date startDate, Date endDate);
}
