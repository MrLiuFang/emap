package com.pepper.service.emap.lift;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.group.GroupBuild;
import com.pepper.model.emap.lift.Floor;

import java.util.List;

public interface FloorService extends BaseService<Floor> {

    public List<Floor> findByLiftId(String liftId);

    public Floor findByName (String name);

    public Floor find(String liftId,String floorName);
}
