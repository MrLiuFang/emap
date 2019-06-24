package com.pepper.model.emap.vo;

import java.util.List;

import com.pepper.model.emap.department.DepartmentGroup;

public class DepartmentGroupVo2 extends DepartmentGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3080388480916364529L;
	private List<AdminUserEventAssistVo> user;
	public List<AdminUserEventAssistVo> getUser() {
		return user;
	}
	public void setUser(List<AdminUserEventAssistVo> user) {
		this.user = user;
	}
	

	
}
