package com.pepper.service.emap.log;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.ICurrentUser;
import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.log.SystemLogDao;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.log.SystemLog;

@Service(interfaceClass=SystemLogService.class)
public class SystemLogServiceImpl extends BaseServiceImpl<SystemLog> implements SystemLogService {
	
	@Resource
	private ICurrentUser currentUser;
	
	@Resource
	private SystemLogDao systemLogDao;

	public void log(String actionName,String url){
		AdminUser user = (AdminUser) currentUser.getCurrentUser();
		if(user==null) {
			return;
		}
		SystemLog systemLog = new SystemLog();
		systemLog.setUserId(user.getId());
		systemLog.setUserName(user.getName());
		systemLog.setLogContent(actionName);
		systemLog.setUrl(url);
		this.save(systemLog);
	}
	
	public void log(String actionName,String url,String data){
		AdminUser user = (AdminUser) currentUser.getCurrentUser();
		SystemLog systemLog = new SystemLog();
		systemLog.setUserId(user.getId());
		systemLog.setUserName(user.getName());
		systemLog.setLogContent(actionName);
		systemLog.setData(data);
		systemLog.setUrl(url);
		this.save(systemLog);
	}
	
	public void log(String actionName,String url,AdminUser adminUser){
		SystemLog systemLog = new SystemLog();
		systemLog.setUserId(adminUser.getId());
		systemLog.setUserName(adminUser.getName());
		systemLog.setLogContent(actionName);
		systemLog.setUrl(url);
		this.save(systemLog);
	}
	
	
}
