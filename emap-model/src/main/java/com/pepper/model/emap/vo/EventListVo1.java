package com.pepper.model.emap.vo;

import com.pepper.model.emap.event.EventList;

public class EventListVo1 extends EventList {

	/**
	 * 
	 */
	private String videoUrl;
	
	
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	private static final long serialVersionUID = -6898102413233580985L;
	private Boolean isAssistFinish;
	public Boolean getIsAssistFinish() {
		return isAssistFinish;
	}
	public void setIsAssistFinish(Boolean isAssistFinish) {
		this.isAssistFinish = isAssistFinish;
	}
	
	
}
