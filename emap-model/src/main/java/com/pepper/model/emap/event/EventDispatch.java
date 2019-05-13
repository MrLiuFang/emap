package com.pepper.model.emap.event;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_event_dispatch")
@DynamicUpdate(true)
public class EventDispatch extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8509500080591176473L;

	@Column(name ="task_id")
	private String taskId;
	
	@Column(name ="event_id")
	private String eventId;
	
	@Column(name ="event_list_id")
	private String eventListId;
	
	@Column(name ="handle_time")
	private Date handleTime; 
	
	@Column(name ="operator")
	private String operator; 
	
	@Column(name ="dispatch_from")
	private String dispatchFrom; 
	
	@Column(name ="title")
	private String title; 
	
	@Column(name ="content")
	private String content;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public Date getHandleTime() {
		return handleTime;
	}

	public void setHandleTime(Date handleTime) {
		this.handleTime = handleTime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getDispatchFrom() {
		return dispatchFrom;
	}

	public void setDispatchFrom(String dispatchFrom) {
		this.dispatchFrom = dispatchFrom;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getEventListId() {
		return eventListId;
	}

	public void setEventListId(String eventListId) {
		this.eventListId = eventListId;
	} 
	
	
	
}
