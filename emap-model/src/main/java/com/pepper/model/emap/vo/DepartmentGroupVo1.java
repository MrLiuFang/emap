package com.pepper.model.emap.vo;

import java.util.List;

import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.department.DepartmentGroup;

public class DepartmentGroupVo1 extends DepartmentGroup  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7104140665103233478L;
	
	private List<AdminUserVo> user;

	public List<AdminUserVo> getUser() {
		return user;
	}

	public void setUser(List<AdminUserVo> user) {
		this.user = user;
	}

	
	
}
