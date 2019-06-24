package com.pepper.service.emap.event.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.event.EventListAssistDao;
import com.pepper.model.emap.event.EventListAssist;
import com.pepper.service.emap.event.EventListAssistService;

@Service(interfaceClass =EventListAssistService.class)
public class EventListAssistServiceImpl extends BaseServiceImpl<EventListAssist> implements EventListAssistService {
	
	@Resource
	private EventListAssistDao eventListAssistDao;

	@Override
	public EventListAssist findEventListAssist(String eventListId, String userId, Boolean isFinish) {
		return eventListAssistDao.findFirstByEventListIdAndUserIdAndIsFinish(eventListId, userId, isFinish);
	}

	@Override
	public EventListAssist findEventListAssist(String eventListId, String userId) {
		return eventListAssistDao.findFirstByEventListIdAndUserId(eventListId, userId);
	}

	@Override
	public List<EventListAssist> findByeventListId(String eventListId) {
		return eventListAssistDao.findByeventListId(eventListId);
	}

}
