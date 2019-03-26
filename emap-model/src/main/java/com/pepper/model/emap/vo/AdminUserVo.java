package com.pepper.model.emap.vo;

import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.console.role.Role;

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

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	
}
