package com.pepper.service.emap.lift.impl;

import com.pepper.core.Pager;
import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.lift.LiftLogDao;
import com.pepper.model.emap.lift.LiftLog;
import com.pepper.service.emap.lift.LiftLogService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Service(interfaceClass = LiftLogService.class)
public class LiftLogServiceImpl extends BaseServiceImpl<LiftLog> implements LiftLogService {

    @Autowired
    private LiftLogDao liftLogDao;

    @Override
    public Pager<LiftLog> List(Pager<LiftLog> pager, String liftId, Date startDate, Date endDate) {
        return liftLogDao.List(pager, liftId, startDate, endDate);
    }
}
