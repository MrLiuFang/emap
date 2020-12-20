package com.pepper.model.emap.vo;

import com.pepper.model.emap.lift.Floor;
import com.pepper.model.emap.lift.Lift;
import com.pepper.model.emap.lift.LiftFloor;

public class LiftFloorVo extends LiftFloor {
    private Lift lift;

    private Floor floor;

    public Lift getLift() {
        return lift;
    }

    public void setLift(Lift lift) {
        this.lift = lift;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }
}
