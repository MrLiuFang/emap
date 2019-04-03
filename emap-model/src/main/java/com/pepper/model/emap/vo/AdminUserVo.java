package com.pepper.model.emap.vo;

import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.console.role.Role;
import com.pepper.model.emap.department.Department;

/**
 * 
 * @author Mr.Liu
 *
 */
public class AdminUserVo extends AdminUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7298384054166818609L;
	private Role role;
	
	private String headPortraitUrl;

	
	private Department department;


	public Department getDepartment() {
		return department;
	}


	public void setDepartment(Department department) {
		this.department = department;
	}
	
	

	public String getHeadPortraitUrl() {
		return headPortraitUrl;
	}


	public void setHeadPortraitUrl(String headPortraitUrl) {
		this.headPortraitUrl = headPortraitUrl;
	}


	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	
}
