package com.pepper.model.emap.vo;

import com.pepper.model.console.admin.user.AdminUser;

public class AdminUserEventAssistVo extends AdminUser{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1306343301926218004L;

	private String headPortraitUrl;
	
	private Boolean isRequestAssist = false;

	public String getHeadPortraitUrl() {
		return headPortraitUrl;
	}

	public void setHeadPortraitUrl(String headPortraitUrl) {
		this.headPortraitUrl = headPortraitUrl;
	}

	public Boolean getIsRequestAssist() {
		return isRequestAssist;
	}

	public void setIsRequestAssist(Boolean isRequestAssist) {
		this.isRequestAssist = isRequestAssist;
	}
	
	
	
}
