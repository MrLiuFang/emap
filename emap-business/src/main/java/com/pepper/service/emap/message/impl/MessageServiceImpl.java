package com.pepper.service.emap.message.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.util.StringUtils;

import com.pepper.service.emap.message.MessageService;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

@Service(interfaceClass = MessageService.class)
public class MessageServiceImpl implements MessageService {

	@Override
	public void send(String deviceId, String title, String message,String eventId,Boolean isAssist,String requestAssistName) {
		if(!StringUtils.hasText(deviceId)) {
			return ;
		}
		Map<String,String> extras = new HashMap<String, String>();
		extras.put("eventId", eventId);
		extras.put("isAssist", isAssist==null?"false":isAssist.toString());
		if(requestAssistName!=null) {
			extras.put("requestAssistName", requestAssistName);
		}
		JPushClient jpushClient = new JPushClient("85501c0630cb06d57e5bd3c1", "c77dc96a2368b516a3c1d922");
		PushPayload payload = PushPayload.newBuilder()
				.setPlatform(Platform.android())
				.setNotification(Notification.android(title,message,extras))
				.setAudience(Audience.registrationId(deviceId))
				.build();
		try {
			PushResult result = jpushClient.sendPush(payload);
//			System.out.print(result);
		} catch (APIConnectionException | APIRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jpushClient.close();
	}

	public static void main(String arg[]) {
//		Map<String,String> extras = new HashMap<String, String>();
//		extras.put("eventId", "2c92b9ad6ab6da1f016ac07267bf0017");
//		JPushClient jpushClient = new JPushClient("85501c0630cb06d57e5bd3c1", "c77dc96a2368b516a3c1d922");
//		PushPayload payload = PushPayload.newBuilder()
//				.setPlatform(Platform.android())
//				.setNotification(Notification.android("测试标题","测试内容",extras))
//				.setAudience(Audience.registrationId("1104a89792ebfe63215"))
//				
//				
////				.setAudience(Audience.registrationId("140fe1da9ee56759b1d"))
//				
////				.setAudience(Audience.all())
//				.build();
//		try {
//			PushResult result = jpushClient.sendPush(payload);
//			System.out.print(result);
//		} catch (APIConnectionException | APIRequestException e) {
//			e.printStackTrace();
//		}
//		jpushClient.close();
		Boolean b = true;
		System.out.print(b.toString());
	}

	@Override
	public void send(String deviceId, String title, String message, String eventId) {
		send(deviceId, title, message, eventId,false,null);
	}
}
