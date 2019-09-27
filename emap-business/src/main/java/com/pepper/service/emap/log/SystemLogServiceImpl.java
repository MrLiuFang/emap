package com.pepper.service.emap.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
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
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = servletRequestAttributes.getRequest();
		AdminUser user = (AdminUser) currentUser.getCurrentUser();
		SystemLog systemLog = new SystemLog();
		systemLog.setUserId(user==null?"":user.getId());
		systemLog.setUserName(user==null?"":user.getName());
		systemLog.setLogContent(actionName);
		systemLog.setUrl(request.getRequestURL().toString());
		
	    if(request.getParameterMap().size()>0) {
	    	ObjectMapper objectMapper = new ObjectMapper();
	    	try {
				systemLog.setData(objectMapper.writeValueAsString(request.getParameterMap()));
			} catch (JsonProcessingException e) {
			}
	    }
	    if(StringUtils.hasText(systemLog.getData())) {
	    	systemLog.setData(systemLog.getData() + " "+ binaryReader(request));
	    }else {
	    	systemLog.setData( binaryReader(request));
	    }
	    
		this.save(systemLog);
	}
	
	public void log(String actionName,String url,String data){
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = servletRequestAttributes.getRequest();
		AdminUser user = (AdminUser) currentUser.getCurrentUser();
		SystemLog systemLog = new SystemLog();
		systemLog.setUserId(user==null?"":user.getId());
		systemLog.setUserName(user==null?"":user.getName());
		systemLog.setLogContent(actionName);
		if(request.getParameterMap().size()>0) {
	    	ObjectMapper objectMapper = new ObjectMapper();
	    	try {
				systemLog.setData(objectMapper.writeValueAsString(request.getParameterMap()));
			} catch (JsonProcessingException e) {
			}
	    }
		if(StringUtils.hasText(systemLog.getData())) {
	    	systemLog.setData(systemLog.getData() + " "+ binaryReader(request));
	    }else {
	    	systemLog.setData( binaryReader(request));
	    }
		systemLog.setUrl(request.getRequestURL().toString());
		this.save(systemLog);
	}
	
	public void log(String actionName,String url,AdminUser adminUser){
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = servletRequestAttributes.getRequest();
		SystemLog systemLog = new SystemLog();
		systemLog.setUserId(adminUser==null?"":adminUser.getId());
		systemLog.setUserName(adminUser==null?"":adminUser.getName());
		systemLog.setLogContent(actionName);
		systemLog.setUrl(request.getRequestURL().toString());
		if(request.getParameterMap().size()>0) {
	    	ObjectMapper objectMapper = new ObjectMapper();
	    	try {
				systemLog.setData(objectMapper.writeValueAsString(request.getParameterMap()));
			} catch (JsonProcessingException e) {
			}
	    }
		if(StringUtils.hasText(systemLog.getData())) {
	    	systemLog.setData(systemLog.getData() + " "+ binaryReader(request));
	    }else {
	    	systemLog.setData( binaryReader(request));
	    }
		this.save(systemLog);
	}
	
	

	private String binaryReader(HttpServletRequest request) {
		
		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(request.getInputStream(), writer, StandardCharsets.UTF_8.name());
			String str = writer.toString();
			return str;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
		
	}
	
}
