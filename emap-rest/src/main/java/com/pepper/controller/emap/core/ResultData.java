package com.pepper.controller.emap.core;

import com.pepper.controller.emap.util.Internationalization;

/**
 * 
 * @author Mr.Liu
 *
 */
public class ResultData extends com.pepper.core.ResultData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3661935046254778119L;

	protected String message = messageInternationalization();
	
	@Override
	public String getMessage() {
		return this.message;
	}
	
	@Override
	public void setMessage(String message) {
		this.message = message;
	}
	
	private String messageInternationalization() {
		return Internationalization.getMessageInternationalization(200);
	}
}
