package com.pepper.service.emap.staff.impl;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.staff.StaffDao;
import com.pepper.model.emap.staff.Staff;
import com.pepper.service.emap.staff.StaffService;

@Service(interfaceClass = StaffService.class)
public class StaffServiceImpl extends BaseServiceImpl<Staff> implements StaffService {
	
	@Resource
	private StaffDao staffDao;

	@Override
	public Staff findByIdCard(String idCard) {
		return staffDao.findByIdCard(idCard);
	}
}
