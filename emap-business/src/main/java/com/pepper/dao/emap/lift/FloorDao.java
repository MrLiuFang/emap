package com.pepper.dao.emap.lift;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.lift.Floor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FloorDao extends BaseDao<Floor> {

    @Query( "  select t1 from Floor t1 where t1.id in (select t2.floorId from LiftFloor t2 where t2.liftId = ?1 )" )
    public List<Floor> findByLiftIdOrderByFloorDesc(String liftId);

    public Floor findFirstByName(String name);

    @Query(" select t1 from Floor t1 join LiftFloor t2 on t1.id = t2.floorId where t2.liftId =?1 and t1.name = ?2")
    public Floor find(String liftId,String floorName);
}
