package com.pepper.model.emap.scheduler;

import com.pepper.core.base.BaseModel;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @ClassName Scheduler
 * @Description: TODO
 * @Author mr.liu
 * @Date 2020/5/29
 **/
@Entity()
@Table(name = "t_scheduler")
@DynamicUpdate(true)
public class Scheduler extends BaseModel {

    private Integer day;

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }
}
