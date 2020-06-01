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

    private Integer cleanEventDay;

    private Integer cleanLogDay;

    public Integer getCleanEventDay() {
        return cleanEventDay;
    }

    public void setCleanEventDay(Integer cleanEventDay) {
        this.cleanEventDay = cleanEventDay;
    }

    public Integer getCleanLogDay() {
        return cleanLogDay;
    }

    public void setCleanLogDay(Integer cleanLogDay) {
        this.cleanLogDay = cleanLogDay;
    }
}
