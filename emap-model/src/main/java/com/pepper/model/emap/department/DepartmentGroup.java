package com.pepper.model.emap.department;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_department_group")
@DynamicUpdate(true)
public class DepartmentGroup  extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3828492734522682197L;
	
	private String departmentId;
	
	private String code;
	
	private String name;
	
	private String colour;
	
	@Column( name = "labelled_alphabet")
	private String labelledAlphabet;
	
	private String remark;
	
	private String startTime;
	
	private String endTime;
	
	

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	public String getLabelledAlphabet() {
		return labelledAlphabet;
	}

	public void setLabelledAlphabet(String labelledAlphabet) {
		this.labelledAlphabet = labelledAlphabet;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	

}
