package com.pepper.dao.emap.staff;

import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.staff.Staff;

public interface StaffDao extends BaseDao<Staff> {

//	public Staff findByIdCard(String idCard);
	
	public List<Staff> findByIdCard(String idCard);
}
