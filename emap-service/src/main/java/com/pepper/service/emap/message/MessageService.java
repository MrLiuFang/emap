package com.pepper.service.emap.message;

public interface MessageService {

	public void send(String deviceId,String title,String message,String eventId,Boolean isAssist,String requestAssistName);
	public void send(String deviceId,String title,String message,String eventId);
}
