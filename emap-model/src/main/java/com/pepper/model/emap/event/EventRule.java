package com.pepper.model.emap.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_event_rule")
@DynamicUpdate(true)
public class EventRule extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5654373387285472494L;

	@Column(name = "node_id",unique = true)
	private String nodeId;
	
	@Column(name = "node_type_id",unique = true)
	private String nodeTypeId;
	
	@Column(name = "warning_level")
	private Integer warningLevel;
	
	@Column(name = "time_0ut")
	private Integer timeOut;
	
	@Column(name = "from_date_time")
	private String fromDateTime;
	
	@Column(name = "to_date_time")
	private String toDateTime;
	
	@Column(name = "action_list_id")
	private String actionListId;
	
	@Column(name = "is_sms")
	private Boolean isSMS;
	
	@Column(name = "sms_receiver")
	private String sMSReceiver;
	
	@Column(name = "sms_content")
	private String sMSContent;
	
	@Column(name = "is_email")
	private Boolean isEmail;
	
	@Column(name = "email_account")
	private String emailAccount;
	
	@Column(name = "is_we_chat")
	private Boolean isWeChat;
	
	@Column(name = "result")
	private String result;
	
	@Column(name = "department_id")
	private String departmentId;
	
	@Column(name = "special_warning_level")
	private Integer specialWarningLevel;
	
	@Column(name = "special_department_id")
	private String specialDepartmentId;
	
	@Column(name = "urgent_warning_level",nullable=false)
	private Integer urgentWarningLevel;

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public Integer getWarningLevel() {
		return warningLevel;
	}

	public void setWarningLevel(Integer warningLevel) {
		this.warningLevel = warningLevel;
	}

	public Integer getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Integer timeOut) {
		this.timeOut = timeOut;
	}

	public String getFromDateTime() {
		return fromDateTime;
	}

	public void setFromDateTime(String fromDateTime) {
		this.fromDateTime = fromDateTime;
	}

	public String getToDateTime() {
		return toDateTime;
	}

	public void setToDateTime(String toDateTime) {
		this.toDateTime = toDateTime;
	}

	public String getActionListId() {
		return actionListId;
	}

	public void setActionListId(String actionListId) {
		this.actionListId = actionListId;
	}

	public Boolean getIsSMS() {
		return isSMS;
	}

	public void setIsSMS(Boolean isSMS) {
		this.isSMS = isSMS;
	}

	public String getsMSReceiver() {
		return sMSReceiver;
	}

	public void setsMSReceiver(String sMSReceiver) {
		this.sMSReceiver = sMSReceiver;
	}

	public String getsMSContent() {
		return sMSContent;
	}

	public void setsMSContent(String sMSContent) {
		this.sMSContent = sMSContent;
	}

	public Boolean getIsEmail() {
		return isEmail;
	}

	public void setIsEmail(Boolean isEmail) {
		this.isEmail = isEmail;
	}

	public String getEmailAccount() {
		return emailAccount;
	}

	public void setEmailAccount(String emailAccount) {
		this.emailAccount = emailAccount;
	}

	public Boolean getIsWeChat() {
		return isWeChat;
	}

	public void setIsWeChat(Boolean isWeChat) {
		this.isWeChat = isWeChat;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getNodeTypeId() {
		return nodeTypeId;
	}

	public void setNodeTypeId(String nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
	}

	public Integer getSpecialWarningLevel() {
		return specialWarningLevel;
	}

	public void setSpecialWarningLevel(Integer specialWarningLevel) {
		this.specialWarningLevel = specialWarningLevel;
	}

	public String getSpecialDepartmentId() {
		return specialDepartmentId;
	}

	public void setSpecialDepartmentId(String specialDepartmentId) {
		this.specialDepartmentId = specialDepartmentId;
	}

	public Integer getUrgentWarningLevel() {
		return urgentWarningLevel;
	}

	public void setUrgentWarningLevel(Integer urgentWarningLevel) {
		this.urgentWarningLevel = urgentWarningLevel;
	}
	
	

}
