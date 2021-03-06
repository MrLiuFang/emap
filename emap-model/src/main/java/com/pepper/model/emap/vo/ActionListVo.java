package com.pepper.model.emap.vo;

import java.util.List;
import java.util.Map;

import com.pepper.model.emap.event.ActionList;

public class ActionListVo extends ActionList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3718512400453065577L;

	private String imageUrl1;
	
	private String imageUrl2;
	
	private String imageUrl3;
	
	private String voiceUrl1;
	
	private String videoUrl;
	
	private List<HelpListVo> helpList;
	
	private AdminUserVo employee;

	private EventListVo eventList;
	
	private List<Map<String,Object>> assist;

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


	public List<HelpListVo> getHelpList() {
		return helpList;
	}


	public void setHelpList(List<HelpListVo> helpList) {
		this.helpList = helpList;
	}


	public AdminUserVo getEmployee() {
		return employee;
	}


	public void setEmployee(AdminUserVo employee) {
		this.employee = employee;
	}


	public EventListVo getEventList() {
		return eventList;
	}


	public void setEventList(EventListVo eventList) {
		this.eventList = eventList;
	}


	public List<Map<String, Object>> getAssist() {
		return assist;
	}


	public void setAssist(List<Map<String, Object>> assist) {
		this.assist = assist;
	}


	public String getVideoUrl() {
		return videoUrl;
	}


	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	
}
