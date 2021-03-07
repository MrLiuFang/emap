package com.pepper.model.emap.event;

import com.pepper.core.base.BaseModel;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @description:
 * @author: 雄大
 * @create: 2021-02-04 13:30
 */
@Entity()
@Table(name = "t_event_group")
@DynamicUpdate(true)
@DynamicInsert
@Data
public class EventListGroup extends BaseModel {

    @Column(name = "event_group_id")
    private String eventGroupId;

    @Column(name = "node_group_code")
    private String nodeGroupCode;

    @Column(name = "node_id")
    private String nodeId;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "is_master")
    private Boolean isMaster;

    @Column(name = "level")
    private Integer level;

    @Column(name = "status")
    private String status;
}
