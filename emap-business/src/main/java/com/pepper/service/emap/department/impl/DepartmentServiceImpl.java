package com.pepper.service.emap.department.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.model.emap.department.Department;
import com.pepper.service.emap.department.DepartmentService;

@Service(interfaceClass = DepartmentService.class )
public class DepartmentServiceImpl extends BaseServiceImpl<Department> implements DepartmentService {

	@Resource
	private com.pepper.dao.emap.department.DepartmentDao departmentDao;

	@Override
	public Department findByCode(String code) {
		return departmentDao.findByCode(code);
	}

	@Override
	public List<Department> findByName(String name) {
		return departmentDao.findByName(name);
	}

	@Override
	public Department findDepartment(String name) {
		return departmentDao.findFirstByName(name);
	}
}
