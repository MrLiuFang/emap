package com.pepper.dao.emap.lift;

import com.pepper.core.Pager;
import com.pepper.model.emap.lift.LiftRightVip;

public interface LiftRightVipDaoEx {
    public Pager<LiftRightVip> List(Pager<LiftRightVip> pager, String staffName, String staffIdCard, String liftName);
}
