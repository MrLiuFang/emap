package com.pepper.model.emap.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_event_message")
@DynamicUpdate(true)
public class EventMessage extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2896749360306688047L;
	
	@Column(name = "message")
	private String message;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "event_list_id")
	private String eventListId;
	
	@Column(name = "event_id")
	private String eventId;
	
	@Column(name = "mobile")
	private String mobile;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "type")
	private Integer type;
	
	@Column(name = "is_send")
	private Boolean isSend;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEventListId() {
		return eventListId;
	}

	public void setEventListId(String eventListId) {
		this.eventListId = eventListId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Boolean getIsSend() {
		return isSend;
	}

	public void setIsSend(Boolean isSend) {
		this.isSend = isSend;
	}
	

}
