package com.pepper.service.emap.lift;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.lift.Floor;
import com.pepper.model.emap.lift.Lift;

public interface LiftService extends BaseService<Lift> {

    public Lift findByFloorId(String floorId);

    public Lift findByName(String name);

    public void updateDownloadTime(String id);
}
