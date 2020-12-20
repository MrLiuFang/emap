package com.pepper.service.emap.lift.impl;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.lift.LiftFloorDao;
import com.pepper.dao.emap.lift.LiftRightDao;
import com.pepper.model.emap.lift.LiftRight;
import com.pepper.service.emap.lift.LiftRightService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service(interfaceClass = LiftRightService.class)
public class LiftRightServiceImpl extends BaseServiceImpl<LiftRight> implements LiftRightService {

    @Autowired
    private LiftRightDao liftRightDao;

    @Override
    @Transactional
    public void delete(String staffId, String liftId, String floorId) {
        liftRightDao.deleteByStaffIdAndLiftIdAndFloorId(staffId, liftId, floorId);
    }

    @Override
    @Transactional
    public void delete(String liftId, String floorId) {
        liftRightDao.deleteByLiftIdAndFloorId(liftId, floorId);
    }

    @Override
    @Transactional
    public void delete(String liftId) {
        liftRightDao.deleteByLiftId(liftId);
    }

    @Override
    @Transactional
    public void deleteByFloorId(String floorId) {
        liftRightDao.deleteByFloorId(floorId);
    }

    @Override
    public LiftRight find(String staffId, String liftId, String floorId) {
        return liftRightDao.findByStaffIdAndLiftIdAndFloorId(staffId, liftId, floorId);
    }


}
