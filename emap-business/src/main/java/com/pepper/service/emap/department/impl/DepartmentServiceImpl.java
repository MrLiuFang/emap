package com.pepper.service.emap.department.impl;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.model.emap.department.Department;
import com.pepper.service.emap.department.DepartmentService;

@Service(interfaceClass = DepartmentService.class )
public class DepartmentServiceImpl extends BaseServiceImpl<Department> implements DepartmentService {

	
}
