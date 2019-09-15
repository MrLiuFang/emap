package com.pepper.service.emap.log;

import java.io.BufferedReader;
import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
		SystemLog systemLog = new SystemLog();
		systemLog.setUserId(user==null?"":user.getId());
		systemLog.setUserName(user==null?"":user.getName());
		systemLog.setLogContent(actionName);
		systemLog.setUrl(url);
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = servletRequestAttributes.getRequest();
	    if(request.getParameterMap().size()>0) {
	    	ObjectMapper objectMapper = new ObjectMapper();
	    	try {
				systemLog.setData(objectMapper.writeValueAsString(request.getParameterMap()));
			} catch (JsonProcessingException e) {
			}
	    }
	    BufferedReader br = null;
		try {
			br = request.getReader();
			String str, wholeStr = "";
		    while((str = br.readLine()) != null){
		    	wholeStr += str;
		    }
		    if(StringUtils.hasText(systemLog.getData())) {
		    	systemLog.setData(systemLog.getData() + " "+ wholeStr);
		    }
		} catch (IOException e) {
		}finally {
			try {
				if(br!=null) {
					br.close();
				}
			} catch (IOException e) {
			}
		}
	    
		this.save(systemLog);
	}
	
	public void log(String actionName,String url,String data){
		AdminUser user = (AdminUser) currentUser.getCurrentUser();
		SystemLog systemLog = new SystemLog();
		systemLog.setUserId(user==null?"":user.getId());
		systemLog.setUserName(user==null?"":user.getName());
		systemLog.setLogContent(actionName);
		systemLog.setData(data);
		systemLog.setUrl(url);
		this.save(systemLog);
	}
	
	public void log(String actionName,String url,AdminUser adminUser){
		SystemLog systemLog = new SystemLog();
		systemLog.setUserId(adminUser==null?"":adminUser.getId());
		systemLog.setUserName(adminUser==null?"":adminUser.getName());
		systemLog.setLogContent(actionName);
		systemLog.setUrl(url);
		this.save(systemLog);
	}
	
	
}
