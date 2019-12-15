package com.pepper.model.emap.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_report")
@DynamicUpdate(true)
public class Report extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7911210642946276678L;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "file")
	private String file;
	
	@Column(name = "remark")
	private String remark;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
