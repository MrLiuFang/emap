package com.pepper.service.emap.lift.impl;

import com.pepper.core.Pager;
import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.lift.LiftRightVipDao;
import com.pepper.model.emap.lift.LiftRightVip;
import com.pepper.service.emap.lift.LiftRightVipService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service(interfaceClass = LiftRightVipService.class)
public class LiftRightVipServiceImpl extends BaseServiceImpl<LiftRightVip> implements LiftRightVipService {

    @Autowired
    private LiftRightVipDao liftRightVipDao;

    @Override
    public Pager<LiftRightVip> List(Pager<LiftRightVip> pager, String staffName, String staffIdCard, String liftName) {
        return liftRightVipDao.List(pager, staffName, staffIdCard, liftName);
    }
}
