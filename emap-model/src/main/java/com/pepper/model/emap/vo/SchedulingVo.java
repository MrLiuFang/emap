package com.pepper.model.emap.vo;

import com.pepper.model.emap.scheduling.Scheduling;

public class SchedulingVo extends Scheduling {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6505728081313703279L;

	private String userName;
	
	private String departmentName;
	
	private String departmentGroupName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDepartmentGroupName() {
		return departmentGroupName;
	}

	public void setDepartmentGroupName(String departmentGroupName) {
		this.departmentGroupName = departmentGroupName;
	}
	
	
}
