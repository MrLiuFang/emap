package com.pepper.model.emap.vo;

import java.util.List;

import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.node.Node;

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
	
	private AdminUser operatorVo;
	
	private AdminUser currentHandleUserVo;
	
	private Boolean isUrgent;
	
	private String assignHeadPortrait;

	public NodeVo getNode() {
		return node;
	}

	public void setNode(NodeVo node) {
		this.node = node;
	}

	public AdminUser getOperatorVo() {
		return operatorVo;
	}

	public void setOperatorVo(AdminUser operatorVo) {
		this.operatorVo = operatorVo;
	}

	public AdminUser getCurrentHandleUserVo() {
		return currentHandleUserVo;
	}

	public void setCurrentHandleUserVo(AdminUser currentHandleUserVo) {
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

	
	
}
