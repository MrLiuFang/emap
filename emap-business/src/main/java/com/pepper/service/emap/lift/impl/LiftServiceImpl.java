package com.pepper.service.emap.lift.impl;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.lift.LiftDao;
import com.pepper.model.emap.lift.Lift;
import com.pepper.service.emap.lift.LiftService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service(interfaceClass = LiftService.class)
public class LiftServiceImpl extends BaseServiceImpl<Lift> implements LiftService {

    @Autowired
    private LiftDao liftDao;

    @Override
    public Lift findByFloorId(String floorId) {
        return liftDao.findByFloorId(floorId);
    }

    @Override
    public Lift findByName(String name) {
        return liftDao.findByName(name);
    }

    @Override
    @Transactional
    public void updateDownloadTime(String id) {
        liftDao.updateDownloadTime(id);
    }
}
