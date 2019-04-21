package com.pepper.controller.emap.front.event;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pepper.service.emap.event.EventListService;

@Controller
@RequestMapping("/front/event")
public class EventListController {

	@Reference
	private EventListService eventListService;
	
//	@RequestMapping("/list")
//	@ResponseBody
//	public Object list(String status,Boolean isMe,Boolean is) {
//		
//	}
	
	
}
