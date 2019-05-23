package com.pepper.model.emap.vo;

import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.department.DepartmentGroup;

public class DepartmentGroupVo extends DepartmentGroup  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7104140665103233478L;
	
	private Department department;

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}
	
	

}
