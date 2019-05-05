package com.pepper.model.emap.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_event_list")
@DynamicUpdate(true)
public class EventList extends BaseModel  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 377575813112583428L;
	
	@Column(name = "event_id")
	private String eventId;
	
	@Column(name = "event_name")
	private String eventName;
	
	@Column(name = "source")
	private String source;
	
	@Column(name = "source_code")
	private String sourceCode;
	
	@Column(name = "warning_level")
	private Integer warningLevel;
	
	@Column(name = "operator")
	private String operator;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "current_handle_user")
	private String currentHandleUser;
	
	@Column(name = "help_id")
	private String helpId;
	
	@Column(name = "content")
	private String content;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public Integer getWarningLevel() {
		return warningLevel;
	}

	public void setWarningLevel(Integer warningLevel) {
		this.warningLevel = warningLevel;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCurrentHandleUser() {
		return currentHandleUser;
	}

	public void setCurrentHandleUser(String currentHandleUser) {
		this.currentHandleUser = currentHandleUser;
	}

	public String getHelpId() {
		return helpId;
	}

	public void setHelpId(String helpId) {
		this.helpId = helpId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
