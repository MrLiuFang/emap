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
	public List<EventListAssist> findEventListAssist(String eventListId) {
		return eventListAssistDao.findByEventListId(eventListId);
	}

	@Override
	public List<EventListAssist> findEventListAssist(String eventListId, Boolean isFinish) {
		return eventListAssistDao.findByEventListIdAndIsFinish(eventListId, isFinish);
	}

	@Override
	public EventListAssist findEventListAssist(String eventListId, String userId,
			String employeeId,Boolean isFinish) {
		return eventListAssistDao.findFirstByEventListIdAndUserIdAndEmployeeIdAndIsFinish(eventListId, userId, employeeId,isFinish);
	}

	@Override
	public EventListAssist findEventListAssist(String eventListId, String userId,
			String employeeId) {
		return eventListAssistDao.findFirstByEventListIdAndUserIdAndEmployeeId(eventListId, userId, employeeId);
	}
	
	@Override
	public List<EventListAssist> findByActionListId(String actionListId) {
		return eventListAssistDao.findByActionListId(actionListId);
	}
	
	@Override
	public List<EventListAssist> findByEventListIdAndEmployeeId(String eventListId, String employeeId) {
		return eventListAssistDao.findByEventListIdAndEmployeeId(eventListId, employeeId);
	}


}
