package com.pepper.model.emap.vo;

import com.pepper.model.emap.lift.Lift;
import com.pepper.model.emap.lift.LiftRightVip;
import com.pepper.model.emap.staff.Staff;

public class LiftRightVipVo extends LiftRightVip {

    private Staff staff;

    private Lift lift;

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Lift getLift() {
        return lift;
    }

    public void setLift(Lift lift) {
        this.lift = lift;
    }
}
