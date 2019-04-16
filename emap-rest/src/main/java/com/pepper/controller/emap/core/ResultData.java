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
	
	private Integer code = 200;
	
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

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
	
	
}
