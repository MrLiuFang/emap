package com.pepper.model.emap.vo;

import com.pepper.model.emap.lift.Floor;
import com.pepper.model.emap.lift.Lift;
import com.pepper.model.emap.lift.LiftRight;

import java.util.List;

public class LiftRightVo extends LiftRight {
    private Lift lift;

    private List<FloorVo> floors;

    public Lift getLift() {
        return lift;
    }

    public void setLift(Lift lift) {
        this.lift = lift;
    }

    public List<FloorVo> getFloors() {
        return floors;
    }

    public void setFloors(List<FloorVo> floors) {
        this.floors = floors;
    }
}
