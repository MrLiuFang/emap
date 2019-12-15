package com.pepper.service.emap.event.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.event.EventMessageDao;
import com.pepper.model.emap.event.EventMessage;
import com.pepper.service.emap.event.EventMessageService;

@Service(interfaceClass=EventMessageService.class)
public class EventMessageServiceImpl extends BaseServiceImpl<EventMessage>  implements EventMessageService {


	@Resource
	private EventMessageDao eventMessageDao;

	@Override
	public List<EventMessage> findEventMessage(String eventListId) {
		return eventMessageDao.findByEventListIdOrderByCreateDateDesc(eventListId);
	}

}
