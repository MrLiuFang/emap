package com.pepper.service.emap.department;

import java.util.List;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.department.Department;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface DepartmentService extends BaseService<Department> {
	/**
	 * 根据code查询部门
	 * @param code
	 * @return
	 */
	public Department findByCode(String code);
	
	public List<Department> findByName(String name);

}
