package com.pepper.service.emap.lift;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseService;
import com.pepper.model.emap.lift.LiftRightVip;

public interface LiftRightVipService extends BaseService<LiftRightVip> {
    public Pager<LiftRightVip> List(Pager<LiftRightVip> pager, String staffName, String staffIdCard, String liftName);
}
