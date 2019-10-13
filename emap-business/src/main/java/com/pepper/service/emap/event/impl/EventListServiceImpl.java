package com.pepper.service.emap.event.impl;

import java.util.Date;
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

	@Override
	public Pager<EventList> doorAttendance(Pager<EventList> pager, String eventListId,String nodeId, Date startDate, Date endDate,
			String staffId) {
		return eventListDao.doorAttendance(pager, eventListId,nodeId, startDate, endDate, staffId);
	}

	@Override
	public Pager<EventList> assistEventList(Pager<EventList> pager, String userId,Boolean isFinish) {
		return eventListDao.assistEventList(pager, userId,isFinish);
	}

	@Override
	public Pager<EventList> historyEventList(Pager<EventList> pager, String event, Integer warningLevel, String node,
			String nodeType, String mapName, String buildName, String stieName, String operator, String status) {
		return eventListDao.historyEventList(pager,null,null, event, warningLevel, node, nodeType, mapName, buildName, stieName, operator, status,null,null,null,null,null);
	}

	@Override
	public Pager<EventList> report(Pager<EventList> pager, Date eventStartDate, Date eventEndDate, String event,
			Integer warningLevel, String node, String nodeTypeId, String mapName, String buildName, String siteName,
			String operatorId, String status, String employeeId,Boolean isOrder,String sortBy,Boolean isSpecial,Boolean isUrgent) {
		return eventListDao.historyEventList(pager, eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId, mapName, buildName, siteName, operatorId, status, employeeId, isOrder,sortBy,isSpecial,isUrgent);
	}

	@Override
	public EventList findOneByNodeId(String nodeId) {
		return eventListDao.findOneByNodeId(nodeId);
	}
	

}
