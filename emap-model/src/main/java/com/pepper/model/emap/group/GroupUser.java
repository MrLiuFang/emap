package com.pepper.model.emap.group;

import com.pepper.core.base.BaseModel;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @description:  分组用户
 * @author: Mr.Liu
 * @create: 2020-06-24 10:14
 */
@Entity()
@Table(name = "t_group_user")
@DynamicUpdate(true)
public class GroupUser extends BaseModel {

    private String groupId;

    private String userId;

    private Boolean isLeader;

    private Boolean isOperator;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getLeader() {
        return isLeader;
    }

    public void setLeader(Boolean leader) {
        isLeader = leader;
    }

    public Boolean getOperator() {
        return isOperator;
    }

    public void setOperator(Boolean operator) {
        isOperator = operator;
    }
}
