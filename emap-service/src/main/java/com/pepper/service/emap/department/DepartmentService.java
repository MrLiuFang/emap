package com.pepper.service.emap.department;

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

}
