package com.pepper.model.emap.lift;

import com.pepper.core.base.BaseModel;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity()
@Table(name = "t_lift_right")
@DynamicUpdate(true)
public class LiftRight extends BaseModel {

    @Column(name = "staff_id")
    private String staffId;

    @Column(name = "lift_id")
    private String liftId;

    @Column(name = "floor_id")
    private String floorId;

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getLiftId() {
        return liftId;
    }

    public void setLiftId(String liftId) {
        this.liftId = liftId;
    }

    public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }
}
