package com.pepper.model.emap.vo;

import java.util.Date;
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
	
	private String nodeName;
	
	private AdminUser operatorVo;
	
	private AdminUser currentHandleUserVo;
	
	private Boolean isUrgent;
	
	private String assignHeadPortrait;
	
	private Date dispatchDate;
	
	private String imageUrl1;
	
	private String imageUrl2;
	
	private String imageUrl3;
	
	
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	private String voiceUrl1;

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

	public Date getDispatchDate() {
		return dispatchDate;
	}

	public void setDispatchDate(Date dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public String getImageUrl1() {
		return imageUrl1;
	}

	public void setImageUrl1(String imageUrl1) {
		this.imageUrl1 = imageUrl1;
	}

	public String getImageUrl2() {
		return imageUrl2;
	}

	public void setImageUrl2(String imageUrl2) {
		this.imageUrl2 = imageUrl2;
	}

	public String getImageUrl3() {
		return imageUrl3;
	}

	public void setImageUrl3(String imageUrl3) {
		this.imageUrl3 = imageUrl3;
	}

	public String getVoiceUrl1() {
		return voiceUrl1;
	}

	public void setVoiceUrl1(String voiceUrl1) {
		this.voiceUrl1 = voiceUrl1;
	}

	
	
}
