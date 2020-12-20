package com.pepper.service.emap.event.impl;

import java.util.*;

import javax.annotation.Resource;

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
	public List<EventList> findByStatus(String status) {
		return eventListDao.findByStatus(status);
	}

	@Override
	public List<EventList> findByStatusNot(String status) {
		return eventListDao.findByStatusNot(status);
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
	public Pager<EventList> transferList(Pager<EventList> pager, String dispatchFrom,String eventId,Boolean isUrgent,String nodeName,String eventName,Date startDate,Date endDate) {
		return eventListDao.transferList(pager, dispatchFrom, eventId, isUrgent, nodeName, eventName, startDate, endDate);
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
	public Pager<EventList> historyEventList(Pager<EventList> pager, String event, Integer startWarningLevel, Integer endWarningLevel, String node,
											 String nodeType, String mapName, String buildName, String stieName, String operator, String status, String eventId, Date startDate, Date endDate, String departmentId) {
		return eventListDao.historyEventList(pager,startDate,endDate, event, startWarningLevel, endWarningLevel, node, nodeType, mapName, buildName, stieName, operator, status, null, null, null, null, null, eventId, departmentId);
	}

	@Override
	public Pager<EventList> report(Pager<EventList> pager, Date eventStartDate, Date eventEndDate, String event,
								   Integer startWarningLevel, Integer endWarningLevel, String node, String nodeTypeId, String mapName, String buildName, String siteName,
								   String operatorId, String status, String employeeId, Boolean isOrder, String sortBy, Boolean isSpecial, Boolean isUrgent) {
		return eventListDao.historyEventList(pager, eventStartDate, eventEndDate, event, startWarningLevel, endWarningLevel, node, nodeTypeId, mapName, buildName, siteName, operatorId, status, employeeId, isOrder, sortBy, isSpecial, isUrgent, null, null);
	}

	@Override
	public EventList findOneByNodeId(String nodeId) {
		return eventListDao.findFirstByNodeId(nodeId);
	}

	@Override
	public Pager<EventList> appList(Pager<EventList> pager, Boolean isFinish, Boolean isUrgent, String eventId, String nodeName,
			String eventName, Date startDate, Date endDate,String currentHandleUser) {
		return eventListDao.appList(pager,isFinish, isUrgent, eventId, nodeName, eventName, startDate, endDate,currentHandleUser);
	}

	@Override
	public Integer toMeNoFiledCount(String userId) {
		return this.eventListDao.countByCurrentHandleUserAndStatusNot(userId,"P");
	}

	@Override
	public Integer todaySpecialCount(Date startDate, Date endDate) {
		return eventListDao.todaySpecialCount(startDate,endDate);
	}

	@Override
	public Integer todayUrgentCount(Date startDate, Date endDate) {
		return this.eventListDao.todayUrgentCount(startDate,endDate);
	}

	@Override
	public Integer todayOrdinaryCount(Date startDate, Date endDate) {
		return this.eventListDao.todayOrdinaryCount(startDate,endDate);
	}

	@Override
	public List<Map<String, Object>> currentMonthCount(Date startDate, Date endDate) {
		return eventListDao.currentMonthCount(startDate,endDate);
	}

	@Override
	public List<Map<String, Object>> currentMonthCount(Date startDate, Date endDate, Boolean isConsole) {
		return eventListDao.currentMonthIsConsoleCount(startDate,endDate);
	}

	@Override
	public List<Map<String, Object>> currentMonthNotRelationNode(Date startDate, Date endDate) {
		return eventListDao.currentMonthNotRelationNode(startDate, endDate);
	}

	@Override
	public List<Map<String, Object>> yearTypeCount(String where, Date startDate, Date endDate) {
		return eventListDao.yearTypeCount(where,startDate,endDate);
	}

	@Override
	public int delete(Date createDate) {
		return eventListDao.deleteByCreateDateLessThanEqual(createDate);
	}

	@Override
	public List<String> userNode(String userId) {
		List<String> list = eventListDao.userNode(userId);
		if(Objects.isNull(list) || list.size()<=0) {
			list = new ArrayList<String>();
			list.add(UUID.randomUUID().toString());
		}
		return list;
	}
}
