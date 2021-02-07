package com.pepper.model.emap.node;

import com.pepper.core.base.BaseModel;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @description:
 * @author: 雄大
 * @create: 2021-02-04 13:26
 */
@Entity()
@Table(name = "t_node_group")
@DynamicUpdate(true)
@Data
public class NodeGroup extends BaseModel {
    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "node_id")
    private String nodeId;

    public NodeGroup() {
    }

    public NodeGroup(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
