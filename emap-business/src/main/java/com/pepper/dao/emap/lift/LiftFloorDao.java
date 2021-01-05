package com.pepper.dao.emap.lift;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.lift.LiftFloor;

public interface LiftFloorDao extends BaseDao<LiftFloor> {

    public LiftFloor findLiftFloorByLiftIdAndFloorId(String liftId,String floorId );

    public void deleteByLiftId(String liftId);

    public void deleteByLiftIdAndFloorId(String liftId,String floorId);

    public void deleteByFloorId(String floorId);
}
