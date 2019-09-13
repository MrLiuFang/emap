package com.pepper.model.emap.vo;

import java.util.List;

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
	
	private List<Role> role;
	
	private String headPortraitUrl;
	private Boolean isRequestAssist = false;
	
	private Department department;
	
	private com.pepper.model.emap.department.DepartmentGroup departmentGroup;


	public Boolean getIsRequestAssist() {
		return isRequestAssist;
	}


	public void setIsRequestAssist(Boolean isRequestAssist) {
		this.isRequestAssist = isRequestAssist;
	}


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
	

	public List<Role> getRole() {
		return role;
	}


	public void setRole(List<Role> role) {
		this.role = role;
	}


	public com.pepper.model.emap.department.DepartmentGroup getDepartmentGroup() {
		return departmentGroup;
	}


	public void setDepartmentGroup(com.pepper.model.emap.department.DepartmentGroup departmentGroup) {
		this.departmentGroup = departmentGroup;
	}
	
	
}
