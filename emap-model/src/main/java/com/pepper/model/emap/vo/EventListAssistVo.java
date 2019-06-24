package com.pepper.model.emap.vo;

import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventListAssist;

public class EventListAssistVo extends EventListAssist {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2663308737590675676L;
	
	private AdminUser user;
	
	private ActionListVo actionList;


	public AdminUser getUser() {
		return user;
	}

	public void setUser(AdminUser user) {
		this.user = user;
	}

	public ActionListVo getActionList() {
		return actionList;
	}

	public void setActionList(ActionListVo actionList) {
		this.actionList = actionList;
	}

	
	
}
