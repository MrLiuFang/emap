package com.pepper.service.emap.event.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.event.ActionListDao;
import com.pepper.model.emap.event.ActionList;
import com.pepper.service.emap.event.ActionListService;

@Service(interfaceClass =ActionListService.class)
public class ActionListServiceImpl extends BaseServiceImpl<ActionList> implements ActionListService {
	
	@Resource
	private ActionListDao actionListDao;

	@Override
	public List<ActionList> findByEventId(String eventId) {
		return actionListDao.findFirstByEventId(eventId);
	}

	@Override
	public List<ActionList> findByEventListId(String eventListId) {
		return actionListDao.findByEventListId(eventListId);
	}

	@Override
	public ActionList findActionList(String eventListId) {
		return actionListDao.findOneByEventListIdOrderByCreateDateDesc(eventListId);
	}

//	@Override
//	public ActionList findActionList(String eventListAssistId) {
//		return actionListDao.findFirstByEventListAssistId(eventListAssistId);
//	}

}
