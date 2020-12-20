package com.pepper.model.emap.event;

import java.time.LocalDateTime;
import java.util.Date;

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
	
	
	@Column(name = "event_date")
	private String eventDate;
	
	@Column(name = "source")
	private String source;
	
	@Column(name = "source_code",nullable=false)
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
	
	@Column(name = "filed_content")
	private String filedContent;
	
	@Column(name = "is_operator_transfer")
	private Boolean isOperatorTransfer;
	
	@Column(name = "operator_transfer_read")
	private Boolean operatorTransferRead;
	
	@Column(name = "operator_transfer_date")
	private Date operatorTransferDate;

	@Column(name ="assign_date")
	private Date assignDate;
	
	@Column(name ="is_special")
	private Boolean isSpecial;
	
	@Column(name ="is_urgent")
	private Boolean isUrgent;
	
	@Column(name ="is_not_found_employee")
	private Boolean isNotFoundEmployee;
	
	@Column(name ="is_not_found_event_rule")
	private Boolean isNotFoundEventRule;
	
	@Column(name = "id_card")
	private String idCard;
	
	@Column(name ="is_unable_finish")
	private Boolean isUnableFinish;
	
	@Column(name ="video")
	private String video;

	@Column(name ="is_Console")
	private Boolean isConsole;

	@Column(name ="filed_date")
	private Date filedDate;

	@Column(name ="filed_user_no")
	private String filedUserNo;

	@Column(name ="dispatch_status")
	private Integer dispatchStatus;

	@Column(name ="apply_date")
	private LocalDateTime applyDate;

	public LocalDateTime getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(LocalDateTime applyDate) {
		this.applyDate = applyDate;
	}

	public String getFiledUserNo() {
		return filedUserNo;
	}

	public void setFiledUserNo(String filedUserNo) {
		this.filedUserNo = filedUserNo;
	}

	public Boolean getConsole() {
		return isConsole;
	}

	public void setConsole(Boolean console) {
		isConsole = console;
	}

	public Date getFiledDate() {
		return filedDate;
	}

	public void setFiledDate(Date filedDate) {
		this.filedDate = filedDate;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public Date getOperatorTransferDate() {
		return operatorTransferDate;
	}

	public void setOperatorTransferDate(Date operatorTransferDate) {
		this.operatorTransferDate = operatorTransferDate;
	}

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

	public String getFiledContent() {
		return filedContent;
	}

	public void setFiledContent(String filedContent) {
		this.filedContent = filedContent;
	}

	public Boolean getIsOperatorTransfer() {
		return isOperatorTransfer;
	}

	public void setIsOperatorTransfer(Boolean isOperatorTransfer) {
		this.isOperatorTransfer = isOperatorTransfer;
	}

	public Boolean getOperatorTransferRead() {
		return operatorTransferRead;
	}

	public void setOperatorTransferRead(Boolean operatorTransferRead) {
		this.operatorTransferRead = operatorTransferRead;
	}

	public Date getAssignDate() {
		return assignDate;
	}

	public void setAssignDate(Date assignDate) {
		this.assignDate = assignDate;
	}

	public Boolean getIsSpecial() {
		return isSpecial;
	}

	public void setIsSpecial(Boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	public Boolean getIsNotFoundEmployee() {
		return isNotFoundEmployee;
	}

	public void setIsNotFoundEmployee(Boolean isNotFoundEmployee) {
		this.isNotFoundEmployee = isNotFoundEmployee;
	}

	public Boolean getIsNotFoundEventRule() {
		return isNotFoundEventRule;
	}

	public void setIsNotFoundEventRule(Boolean isNotFoundEventRule) {
		this.isNotFoundEventRule = isNotFoundEventRule;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public Boolean getIsUnableFinish() {
		return isUnableFinish;
	}

	public void setIsUnableFinish(Boolean isUnableFinish) {
		this.isUnableFinish = isUnableFinish;
	}

	public Boolean getIsUrgent() {
		return isUrgent;
	}

	public void setIsUrgent(Boolean isUrgent) {
		this.isUrgent = isUrgent;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public Integer getDispatchStatus() {
		return dispatchStatus;
	}

	public void setDispatchStatus(Integer dispatchStatus) {
		this.dispatchStatus = dispatchStatus;
	}
}
