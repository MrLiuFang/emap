package com.pepper.model.emap.vo;

import java.util.List;

import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.department.DepartmentGroup;

public class DepartmentGroupVo1 extends DepartmentGroup  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7104140665103233478L;
	
	private List<AdminUser> user;

	public List<AdminUser> getUser() {
		return user;
	}

	public void setUser(List<AdminUser> user) {
		this.user = user;
	}
	
}
