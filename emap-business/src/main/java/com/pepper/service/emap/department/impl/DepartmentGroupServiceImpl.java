package com.pepper.service.emap.department.impl;

import java.util.List;

import javax.annotation.Resource;
import org.apache.dubbo.config.annotation.Service;
import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.department.DepartmentGroupDao;
import com.pepper.model.emap.department.DepartmentGroup;
import com.pepper.service.emap.department.DepartmentGroupService;

@Service(interfaceClass=DepartmentGroupService.class )
public class DepartmentGroupServiceImpl extends BaseServiceImpl<DepartmentGroup> implements DepartmentGroupService {

	@Resource
	private DepartmentGroupDao departmentGroupDao;

	@Override
	public void deleteByDepartmentId(String departmentId) {
		departmentGroupDao.deleteByDepartmentId(departmentId);
	}

	@Override
	public List<DepartmentGroup> findByDepartmentId(String departmentId) {
		return departmentGroupDao.findByDepartmentId(departmentId);
	}

	@Override
	public List<DepartmentGroup> findByDepartmentIdAndName(String departmentId, String name) {
		return departmentGroupDao.findByDepartmentIdAndName(departmentId, name);
	}
	
	@Override
	public List<DepartmentGroup> findByDepartmentIdAndCode(String departmentId, String code) {
		return departmentGroupDao.findByDepartmentIdAndCode(departmentId, code);
	}

	@Override
	public DepartmentGroup findByCode(String code) {
<<<<<<< HEAD
		return departmentGroupDao.findOneByCode(code);
=======
		return departmentGroupDao.findFirstByCode(code);
>>>>>>> refs/heads/master
	}

}
