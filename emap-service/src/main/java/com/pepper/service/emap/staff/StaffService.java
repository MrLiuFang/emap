package com.pepper.service.emap.staff;

import java.util.List;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.staff.Staff;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface StaffService extends BaseService<Staff> {

	public List<Staff> findByIdCard(String idCard);
}
