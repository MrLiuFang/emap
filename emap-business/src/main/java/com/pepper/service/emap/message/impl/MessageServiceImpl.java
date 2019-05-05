package com.pepper.service.emap.message.impl;

import java.util.HashMap;

import org.apache.dubbo.config.annotation.Service;

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
	public void send(String deviceId, String title, String message) {
		JPushClient jpushClient = new JPushClient("85501c0630cb06d57e5bd3c1", "c77dc96a2368b516a3c1d922");
		PushPayload payload = PushPayload.newBuilder()
				.setPlatform(Platform.android())
				.setNotification(Notification.android(title,message,new HashMap<String, String>()))
				.setAudience(Audience.registrationId(deviceId))
				.build();
		try {
			PushResult result = jpushClient.sendPush(payload);
			System.out.print(result);
		} catch (APIConnectionException | APIRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jpushClient.close();
	}

	public static void main(String arg[]) {
		JPushClient jpushClient = new JPushClient("85501c0630cb06d57e5bd3c1", "c77dc96a2368b516a3c1d922");
		PushPayload payload = PushPayload.newBuilder()
				.setPlatform(Platform.android())
				.setNotification(Notification.android("title","message",new HashMap<String, String>()))
				.setAudience(Audience.registrationId("",""))
//				.setAudience(Audience.all())
				.build();
		try {
			PushResult result = jpushClient.sendPush(payload);
			System.out.print(result);
		} catch (APIConnectionException | APIRequestException e) {
			e.printStackTrace();
		}
		jpushClient.close();
	}
}