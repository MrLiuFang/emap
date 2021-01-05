package com.pepper.model.emap.lift;

import com.pepper.core.base.BaseModel;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity()
@Table(name = "t_floor")
@DynamicUpdate(true)
public class Floor extends BaseModel {

    @Column(name = "name")
    private String name;

    @Column(name = "floor")
    private Integer floor;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }
}
