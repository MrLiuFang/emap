package com.pepper.service.emap.lift.impl;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.lift.FloorDao;
import com.pepper.model.emap.lift.Floor;
import com.pepper.service.emap.lift.FloorService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service(interfaceClass = FloorService.class)
public class FloorServiceImpl extends BaseServiceImpl<Floor> implements FloorService {

    @Autowired
    private FloorDao floorDao;

    @Override
    public List<Floor> findByLiftId(String liftId) {
        return floorDao.findByLiftId(liftId);
    }
}
