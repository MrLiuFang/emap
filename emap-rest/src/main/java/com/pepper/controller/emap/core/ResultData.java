package com.pepper.controller.emap.core;

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
	
	private String messageInternationalization() {
		return "操作成功！";
	}
}
