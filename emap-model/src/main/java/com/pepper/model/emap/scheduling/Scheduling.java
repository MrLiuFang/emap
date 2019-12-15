package com.pepper.model.emap.scheduling;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_scheduling")
@DynamicUpdate(true)
public class Scheduling extends BaseModel  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5174711966752119093L;

	@Column(name = "user_id")
	private String userId;
	
	@Temporal(TemporalType.DATE)
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@Column(name = "date"  )
	private Date date;
	
	@Column(name = "department_id")
	private String departmentId;
	
	@Column(name = "department_group_id")
	private String departmentGroupId;

	@Column(name = "is_manager")
	private Boolean isManager;

	public Boolean getManager() {
		return isManager;
	}

	public void setManager(Boolean manager) {
		isManager = manager;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentGroupId() {
		return departmentGroupId;
	}

	public void setDepartmentGroupId(String departmentGroupId) {
		this.departmentGroupId = departmentGroupId;
	}
	
	
}
