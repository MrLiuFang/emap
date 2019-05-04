package com.pepper.service.emap.event;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.event.EventDispatchDao;
import com.pepper.model.emap.event.EventDispatch;

@Service(interfaceClass =EventDispatchService.class)
public class EventDispatchServiceImpl extends BaseServiceImpl<EventDispatch> implements EventDispatchService {

	@Resource
	private EventDispatchDao eventDispatchDao;
	
	@Override
	public EventDispatch findEventDispatch(String eventId,String operator) {
		return eventDispatchDao.findFirstByEventIdAndOperatorOrderByCreateDateDesc(eventId,operator);
	}

}
