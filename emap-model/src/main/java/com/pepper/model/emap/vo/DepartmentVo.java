package com.pepper.model.emap.vo;

import java.util.List;

import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.department.DepartmentGroup;

public class DepartmentVo extends Department {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3502514241011513350L;

	private List<DepartmentGroup> departmentGroup;

	public List<DepartmentGroup> getDepartmentGroup() {
		return departmentGroup;
	}

	public void setDepartmentGroup(List<DepartmentGroup> departmentGroup) {
		this.departmentGroup = departmentGroup;
	}
	
	
}
