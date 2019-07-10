package com.pepper.model.emap.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.event.ActionList;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventMessage;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.staff.Staff;

/**
 * 
 * @author Mr.Liu
 *
 */
public class EventListVo extends EventList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5936683481323862183L;
	
	private NodeVo node;
	
	private String nodeName;
	
	private AdminUserVo operatorVo;
	
	private AdminUserVo currentHandleUserVo;
	
	private Boolean isUrgent;
	
	private String assignHeadPortrait;
	
	private Date dispatchDate;
	
	private List<ActionListVo> actionList;
	
	private String actionListId;
	
	private Staff staff;
	
	private Boolean isAssistFinish;
	
	private List<EventMessage> eventMessage;
	
//	private List<Map<String,Object>> assist = new ArrayList<Map<String,Object>>();
	
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}


	public NodeVo getNode() {
		return node;
	}

	public void setNode(NodeVo node) {
		this.node = node;
	}

	public AdminUserVo getOperatorVo() {
		return operatorVo;
	}

	public void setOperatorVo(AdminUserVo operatorVo) {
		this.operatorVo = operatorVo;
	}

	public AdminUserVo getCurrentHandleUserVo() {
		return currentHandleUserVo;
	}

	public void setCurrentHandleUserVo(AdminUserVo currentHandleUserVo) {
		this.currentHandleUserVo = currentHandleUserVo;
	}

	public Boolean getIsUrgent() {
		return isUrgent;
	}

	public void setIsUrgent(Boolean isUrgent) {
		this.isUrgent = isUrgent;
	}

	public String getAssignHeadPortrait() {
		return assignHeadPortrait;
	}

	public void setAssignHeadPortrait(String assignHeadPortrait) {
		this.assignHeadPortrait = assignHeadPortrait;
	}

	public Date getDispatchDate() {
		return dispatchDate;
	}

	public void setDispatchDate(Date dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public List<ActionListVo> getActionList() {
		return actionList;
	}

	public void setActionList(List<ActionListVo> actionList) {
		this.actionList = actionList;
	}

	public String getActionListId() {
		return actionListId;
	}

	public void setActionListId(String actionListId) {
		this.actionListId = actionListId;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public Boolean getIsAssistFinish() {
		return isAssistFinish;
	}

	public void setIsAssistFinish(Boolean isAssistFinish) {
		this.isAssistFinish = isAssistFinish;
	}

	public List<EventMessage> getEventMessage() {
		return eventMessage;
	}

	public void setEventMessage(List<EventMessage> eventMessage) {
		this.eventMessage = eventMessage;
	}
	
	

//	public List<Map<String, Object>> getAssist() {
//		return assist;
//	}
//
//	public void setAssist(List<Map<String, Object>> assist) {
//		this.assist = assist;
//	}
	
	

}
