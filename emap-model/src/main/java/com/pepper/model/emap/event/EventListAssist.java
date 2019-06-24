package com.pepper.model.emap.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_event_assist")
@DynamicUpdate(true)
public class EventListAssist extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2489921122028055031L;
	
	@Column(name = "event_list_id")
	private String eventListId;
	
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "assist_remark")
	private String assistRemark;
	
	@Column(name = "is_finish")
	private Boolean isFinish;

	public String getEventListId() {
		return eventListId;
	}

	public void setEventListId(String eventListId) {
		this.eventListId = eventListId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAssistRemark() {
		return assistRemark;
	}

	public void setAssistRemark(String assistRemark) {
		this.assistRemark = assistRemark;
	}

	public Boolean getIsFinish() {
		return isFinish;
	}

	public void setIsFinish(Boolean isFinish) {
		this.isFinish = isFinish;
	}
	
	

}
