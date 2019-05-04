package com.pepper.service.emap.event;

import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.Pager;
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

	@Override
	public Pager<EventList> List(Pager<EventList> pager, Integer warningLevel,Boolean isUrgent,String id) {
		return eventListDao.List(pager, warningLevel,isUrgent,id);
	}

	@Override
	public java.util.List<EventList> findBySourceCodeAndIdNot(String sourceCode, String id) {
		return eventListDao.findBySourceCodeAndIdNot(sourceCode, id);
	}

	@Override
	public void handover(String handoverUserId, String currentUserId) {
		eventListDao.handover(handoverUserId, currentUserId);
	}

	@Override
	public Pager<EventList> transferList(Pager<EventList> pager, String dispatchFrom) {
		return eventListDao.transferList(pager, dispatchFrom);
	}
	

}
