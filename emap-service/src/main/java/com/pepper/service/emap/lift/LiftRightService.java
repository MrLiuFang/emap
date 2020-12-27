package com.pepper.service.emap.lift;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.lift.LiftRight;

public interface LiftRightService extends BaseService<LiftRight> {

    public void delete(String staffId,String liftId,String floorId );
    public void deleteByStaffId(String staffId);
    public void delete(String liftId, String floorId);

    public void delete(String liftId);

    public void deleteByFloorId(String floorId);

    public LiftRight find(String staffId,String liftId,String floorId);
}
