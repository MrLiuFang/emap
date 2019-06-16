package com.pepper.service.emap.event.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.print.attribute.standard.PageRanges;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.pepper.core.Pager;
import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.event.EventListDao;
import com.pepper.model.emap.event.EventList;
import com.pepper.service.emap.event.EventListService;

@Service(interfaceClass=EventListService.class)
public class EventListServiceImpl extends BaseServiceImpl<EventList> implements EventListService {
	
	@Resource
	private EventListDao eventListDao; 

	@Override
	public List<EventList> findByStatusOrStatus(String status, String status1) {
		return eventListDao.findByStatusOrStatus(status, status1);
	}

	@Override
	public Pager<EventList> List(Pager<EventList> pager, Boolean isUrgent) {
		return eventListDao.List(pager,isUrgent);
	}

	@Override
	public Pager<EventList> findBySourceCodeAndIdNot(String sourceCode, String id,Pager<EventList> pager) {
		Pageable pageable =PageRequest.of(pager.getPageNo()-1, pager.getPageSize());
		Page<EventList> page = eventListDao.findBySourceCodeAndIdNotAndWarningLevelNot(sourceCode, id,0,pageable);
		pager.setResults(page.getContent());
		pager.setTotalRow(Long.valueOf(page.getTotalElements()));
		return pager;
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
