package com.pepper.model.emap.vo;

import javax.persistence.Column;

import com.pepper.model.emap.event.EventDispatch;
/**
 * 
 * @author mr.liu
 *
 */
public class EventDispatchVo extends EventDispatch {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5051757469835223556L;

    private String imageUrl1;

    private String imageUrl2;

    private String imageUrl3;

    private String voiceUrl1;

    private String videoUrl;
    
    private String operatorName;
    
    private String dispatchFromName;
    
    private String operatorHeadPortraitUrl;
    
    private String dispatchFromHeadPortraitUrl;

	public String getOperatorHeadPortraitUrl() {
		return operatorHeadPortraitUrl;
	}

	public void setOperatorHeadPortraitUrl(String operatorHeadPortraitUrl) {
		this.operatorHeadPortraitUrl = operatorHeadPortraitUrl;
	}

	public String getDispatchFromHeadPortraitUrl() {
		return dispatchFromHeadPortraitUrl;
	}

	public void setDispatchFromHeadPortraitUrl(String dispatchFromHeadPortraitUrl) {
		this.dispatchFromHeadPortraitUrl = dispatchFromHeadPortraitUrl;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getDispatchFromName() {
		return dispatchFromName;
	}

	public void setDispatchFromName(String dispatchFromName) {
		this.dispatchFromName = dispatchFromName;
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

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
    
    
}
