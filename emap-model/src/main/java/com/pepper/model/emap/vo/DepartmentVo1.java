package com.pepper.model.emap.vo;

import java.util.List;

import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.department.DepartmentGroup;

public class DepartmentVo1 extends Department {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3502514241011513350L;

	private List<DepartmentGroupVo1> departmentGroup;

	public List<DepartmentGroupVo1> getDepartmentGroup() {
		return departmentGroup;
	}

	public void setDepartmentGroup(List<DepartmentGroupVo1> departmentGroup) {
		this.departmentGroup = departmentGroup;
	}

	
}
