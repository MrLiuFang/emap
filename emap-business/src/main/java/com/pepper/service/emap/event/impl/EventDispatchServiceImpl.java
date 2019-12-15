package com.pepper.service.emap.event.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.event.EventDispatchDao;
import com.pepper.model.emap.event.EventDispatch;
import com.pepper.service.emap.event.EventDispatchService;

@Service(interfaceClass =EventDispatchService.class)
public class EventDispatchServiceImpl extends BaseServiceImpl<EventDispatch> implements EventDispatchService {

	@Resource
	private EventDispatchDao eventDispatchDao;
	
	@Override
	public EventDispatch findEventDispatch(String eventId,String operator) {
		return eventDispatchDao.findFirstByEventListIdAndOperatorOrderByCreateDateDesc(eventId,operator);
	}

	@Override
	public EventDispatch findEventDispatch(String eventId, String operator, Boolean isEmployeeTransfer) {
		return eventDispatchDao.findFirstByEventListIdAndOperatorAndIsEmployeeTransferOrderByCreateDateDesc(eventId, operator, isEmployeeTransfer);
	}

	@Override
	public List<EventDispatch> findEventDispatch(String eventListId) {
		return eventDispatchDao.findByEventListIdOrderByCreateDateDesc(eventListId);
	}

}
