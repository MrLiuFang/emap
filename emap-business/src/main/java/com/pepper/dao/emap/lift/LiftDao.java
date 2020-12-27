package com.pepper.dao.emap.lift;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.lift.Lift;
import org.springframework.data.jpa.repository.Query;

public interface LiftDao  extends BaseDao<Lift> {

    @Query(" select t1 from Lift t1 join LiftFloor t2 on t1.id = t2.liftId where t2.floorId = ?1 ")
    public Lift findByFloorId(String floorId);
}
