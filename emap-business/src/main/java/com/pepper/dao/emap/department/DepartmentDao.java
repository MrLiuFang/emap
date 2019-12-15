package com.pepper.dao.emap.department;

import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.department.Department;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface DepartmentDao extends BaseDao<Department> {

	/**
	 * 根据code查询部门
	 * @param code
	 * @return
	 */
	public Department findByCode(String code);
	
	public List<Department> findByName(String name);
	
	public Department findFirstByName(String name);
}
