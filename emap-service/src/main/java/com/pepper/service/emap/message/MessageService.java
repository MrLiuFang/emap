package com.pepper.service.emap.message;

public interface MessageService {

	public void send(String deviceId,String title,String message,String eventId,Boolean isAssist);
	public void send(String deviceId,String title,String message,String eventId);
}
