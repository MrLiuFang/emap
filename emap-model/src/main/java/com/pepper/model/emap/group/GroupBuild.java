package com.pepper.model.emap.group;

import com.pepper.core.base.BaseModel;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @description: 分组建筑
 * @author: Mr.Liu
 * @create: 2020-06-24 10:18
 */
@Entity()
@Table(name = "t_group_build")
@DynamicUpdate(true)
public class GroupBuild extends BaseModel {

    private String groupId;

    private String buildId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }
}
