package com.pepper.model.emap.log;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_system_log")
@DynamicUpdate(true)
public class SystemLog extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8848131674673995142L;

	@Column(name="user_id")
	private String userId;
	
	@Column(name="user_name")
	private String userName;
	
	@Column(name="url")
	private String url;
	
	@Column(name="data")
	private String data;
	
	@Column(name="log_content")
	private String logContent;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
	
	
	
}
