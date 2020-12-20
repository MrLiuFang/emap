package com.pepper.dao.emap.lift;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.lift.Floor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FloorDao extends BaseDao<Floor> {

    @Query( "  select t1 from Floor t1 where t1.id in (select t2.floorId from LiftFloor t2 where t2.liftId = ?1 )" )
    public List<Floor> findByLiftId(String liftId);
}
