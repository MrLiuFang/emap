package com.pepper.dao.emap.department;

import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.department.DepartmentGroup;

public interface DepartmentGroupDao extends BaseDao<DepartmentGroup>{

	
	public void deleteByDepartmentId(String departmentId);
	
	public List<DepartmentGroup> findByDepartmentId(String departmentId);
	
	public List<DepartmentGroup> findByDepartmentIdAndName(String departmentId,String name);
}
