package com.pepper.service.emap.lift.impl;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.lift.LiftFloorDao;
import com.pepper.model.emap.lift.LiftFloor;
import com.pepper.service.emap.lift.LiftFloorSevice;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service(interfaceClass = LiftFloorSevice.class)
public class LiftFloorSeviceImpl extends BaseServiceImpl<LiftFloor> implements LiftFloorSevice {

    @Autowired
    private LiftFloorDao liftFloorDao;

    @Override
    public LiftFloor findLiftFloor(String liftId, String floorId) {
        return liftFloorDao.findLiftFloorByLiftIdAndFloorId(liftId, floorId);
    }

    @Override
    @Transactional
    public void deleteByLiftId(String liftId) {
        liftFloorDao.deleteByLiftId(liftId);
    }

    @Override
    public void deleteByLiftIdAndFloorId(String liftId, String floorId) {
        liftFloorDao.deleteByLiftIdAndFloorId(liftId, floorId);
    }

    @Override
    @Transactional
    public void deleteByFloorId(String floorId) {
        liftFloorDao.deleteByFloorId(floorId);
    }
}
