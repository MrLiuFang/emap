package com.pepper.model.emap.vo;

import com.pepper.model.emap.lift.Floor;
import com.pepper.model.emap.lift.Lift;
import com.pepper.model.emap.lift.LiftRight;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

public class LiftRightVo {

    private String startDate;

    private String endDate;

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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
