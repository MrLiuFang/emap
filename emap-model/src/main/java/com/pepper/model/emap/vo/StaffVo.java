package com.pepper.model.emap.vo;

import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.staff.Staff;

/**
 * 
 * @author Mr.Liu
 *
 */
public class StaffVo extends Staff {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5188464496531346732L;

	
	private Department department;


	public Department getDepartment() {
		return department;
	}


	public void setDepartment(Department department) {
		this.department = department;
	}
	
}
