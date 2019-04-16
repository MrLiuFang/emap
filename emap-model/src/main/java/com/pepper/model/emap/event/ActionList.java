package com.pepper.model.emap.event;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_action_list")
@DynamicUpdate(true)
public class ActionList extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1306017114585728921L;

	@Column(name ="action_list_id")
	private String actionListId;
	
	@Column(name ="task_id")
	private String taskId;
	
	@Column(name ="event_id")
	private String eventId;
	
	@Column(name ="operator")
	private String operator;
	
	@Column(name ="action_type")
	private String actionType;
	
	@Column(name ="receiver")
	private String receiver;
	
	@Column(name ="content")
	private String content;
	
	@Column(name ="retry_times")
	private String retryTimes;
	
	@Column(name ="sent_time")
	private Date sentTime;
	
	@Column(name ="success")
	private Boolean success;
	
	@Column(name ="receive_date")
	private Date receiveDate;
	
	@Column(name ="result")
	private String result;
	
	@Column(name ="status")
	private String status;
	
	@Column(name ="image1")
	private String image1;
	
	@Column(name ="image2")
	private String image2;
	
	@Column(name ="image3")
	private String image3;
	
	@Column(name ="voice1")
	private String voice1;

	public String getActionListId() {
		return actionListId;
	}

	public void setActionListId(String actionListId) {
		this.actionListId = actionListId;
	}

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

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(String retryTimes) {
		this.retryTimes = retryTimes;
	}

	public Date getSentTime() {
		return sentTime;
	}

	public void setSentTime(Date sentTime) {
		this.sentTime = sentTime;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getImage1() {
		return image1;
	}

	public void setImage1(String image1) {
		this.image1 = image1;
	}

	public String getImage2() {
		return image2;
	}

	public void setImage2(String image2) {
		this.image2 = image2;
	}

	public String getImage3() {
		return image3;
	}

	public void setImage3(String image3) {
		this.image3 = image3;
	}

	public String getVoice1() {
		return voice1;
	}

	public void setVoice1(String voice1) {
		this.voice1 = voice1;
	}
	
	
}
