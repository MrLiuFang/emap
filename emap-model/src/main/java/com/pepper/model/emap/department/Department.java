package com.pepper.model.emap.department;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_department")
@DynamicUpdate(true)
public class Department extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1804061198775449157L;
	
	@Column(name = "code",unique = true)
	private String code;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "remark")
	private String remark;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	

}
