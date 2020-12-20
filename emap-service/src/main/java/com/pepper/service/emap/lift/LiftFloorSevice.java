package com.pepper.service.emap.lift;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.lift.Floor;
import com.pepper.model.emap.lift.LiftFloor;

public interface LiftFloorSevice extends BaseService<LiftFloor> {
    public LiftFloor findLiftFloor(String liftId, String floorId);
    public void deleteByLiftId(String liftId);

    public void deleteByFloorId(String floorId);
}
