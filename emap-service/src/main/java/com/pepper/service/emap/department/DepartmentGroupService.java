package com.pepper.service.emap.department;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.department.DepartmentGroup;

public interface DepartmentGroupService extends BaseService<DepartmentGroup> {
	
	@Transactional
	public void deleteByDepartmentId(String departmentId);
	
	public List<DepartmentGroup> findByDepartmentId(String departmentId);
	
	public DepartmentGroup findByCode(String code);

	public List<DepartmentGroup> findByDepartmentIdAndName(String departmentId,String name);
	
	public List<DepartmentGroup> findByDepartmentIdAndCode(String departmentId, String code);
}
