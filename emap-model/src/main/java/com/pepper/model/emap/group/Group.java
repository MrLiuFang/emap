package com.pepper.model.emap.group;

import com.pepper.core.base.BaseModel;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @description: 分组
 * @author: Mr.Liu
 * @create: 2020-06-24 10:04
 */
@Entity()
@Table(name = "t_group")
@DynamicUpdate(true)
public class Group extends BaseModel {

    private String name;

    @Column(unique = true)
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
