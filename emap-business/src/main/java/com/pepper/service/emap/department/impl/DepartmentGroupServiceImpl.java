package com.pepper.service.emap.department.impl;

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

}
