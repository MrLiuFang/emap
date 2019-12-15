package com.pepper.service.emap.log;

import com.pepper.core.base.BaseService;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.log.SystemLog;

public interface SystemLogService extends BaseService<SystemLog> {

	public void log(String actionName,String url);
	public void log(String actionName);
	public void log(String actionName,String url,String data);
	
	public void log(String actionName,String url,AdminUser adminUser);
}
