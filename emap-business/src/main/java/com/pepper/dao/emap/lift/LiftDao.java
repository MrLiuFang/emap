package com.pepper.dao.emap.lift;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.lift.Lift;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LiftDao  extends BaseDao<Lift> {

    @Query(" select t1 from Lift t1 join LiftFloor t2 on t1.id = t2.liftId where t2.floorId = ?1 ")
    public Lift findByFloorId(String floorId);

    public Lift findByName(String name);

    @Query( " update Lift set downloadTime = null  where id = ?1 ")
    @Modifying
    public void updateDownloadTime(String id);
}
