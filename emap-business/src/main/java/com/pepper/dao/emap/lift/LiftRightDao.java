package com.pepper.dao.emap.lift;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.lift.LiftRight;

public interface LiftRightDao extends BaseDao<LiftRight> {

    public void deleteByStaffIdAndLiftIdAndFloorId(String staffId,String liftId,String floorId );

    public void deleteByLiftIdAndFloorId(String liftId,String floorId);

    public void deleteByLiftId(String liftId);

    public void deleteByFloorId(String floorId);

    public LiftRight findByStaffIdAndLiftIdAndFloorId(String staffId,String liftId,String floorId);
}
