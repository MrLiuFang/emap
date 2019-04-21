package com.pepper.service.emap.event;

import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.event.EventListDao;
import com.pepper.model.emap.event.EventList;

@Service(interfaceClass=EventListService.class)
public class EventListServiceImpl extends BaseServiceImpl<EventList> implements EventListService {
	
	@Resource
	private EventListDao eventListDao; 

	@Override
	public List<EventList> findByStatusOrStatus(String status, String status1) {
		return eventListDao.findByStatusOrStatus(status, status1);
	}
	

}
