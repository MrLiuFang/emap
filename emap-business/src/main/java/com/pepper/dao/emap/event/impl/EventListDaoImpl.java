package com.pepper.dao.emap.event.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.google.common.base.Objects;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseDao;
import com.pepper.dao.emap.event.EventListDaoEx;
import com.pepper.model.emap.event.EventList;

/**
 * 
 * @author Mr.Liu
 *
 */
public class EventListDaoImpl  implements EventListDaoEx {

	@Autowired
	private BaseDao<EventList> baseDao;
	
	@Override
	public Pager<EventList> List(Pager<EventList> pager, Boolean isUrgent) {
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		jpql.append(" from EventList t1 where id in ( ");
		jpql.append("  select el.id from  EventList el where el.status = 'N'   AND el.warningLevel  " ).append(isUrgent?">=":"<");
		jpql.append(" (select  er.warningLevel from EventRule er join Node n on er.nodeId=n.id where el.sourceCode=n.sourceCode and t1.id=el.id ) ) ");
		jpql.append( " or id in ( ");
		jpql.append(" select el.id from  EventList el where el.status = 'N'   AND el.warningLevel  " ).append(isUrgent?">=":"<");
		jpql.append(" (select  er.warningLevel from EventRule er join Node n on n.nodeTypeId = er.nodeTypeId where el.sourceCode=n.sourceCode and t1.id=el.id )   ");
		jpql.append(" and el.id not in (select t2.id from EventList t2 join Node t3 on t2.sourceCode = t3.sourceCode join EventRule t4 on t3.id = t4.nodeId where t2.status = 'N'  )   ");
		jpql.append(" ) order by createDate desc   ");
		
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
		
	}

	@Override
	public Pager<EventList> transferList(Pager<EventList> pager,String dispatchFrom) {
		StringBuffer jpql = new StringBuffer();
		jpql.append("select  distinct el from  EventList el join EventDispatch ed on el.id = ed.eventListId where ed.dispatchFrom = :dispatchFrom order by ed.createDate desc ");
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		searchParameter.put("dispatchFrom", dispatchFrom);
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	}


	public Pager<EventList> doorAttendance(Pager<EventList> pager,String eventListId,String nodeId,Date startDate,Date endDate,String staffId){
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		
		jpql.append(" select el from EventList el  ");
		if(StringUtils.hasText(staffId)) {
			jpql.append(" join Staff s on el.idCard = s.idCard  ");
		}
		if(StringUtils.hasText(nodeId)) {
			jpql.append(" join Node n on n.sourceCode = el.sourceCode  ");
		}
		
		jpql.append(" where el.warningLevel=0 ");
		
		if(StringUtils.hasText(eventListId)) {
			Optional<EventList> optional = baseDao.findById(eventListId);
			EventList eventList = optional.isPresent()?optional.get():null;
			if(eventList!=null) {
				jpql.append(" and el.sourceCode =:sourceCode ");
				searchParameter.put("sourceCode", eventList.getSourceCode());
			}else {
				return pager;
			}
		}
		
		if(startDate!=null) {
			jpql.append(" and el.createDate >=:startDate ");
			searchParameter.put("startDate",startDate);
		}
		
		if(endDate!=null) {
			jpql.append(" and el.createDate <=:endDate ");
			searchParameter.put("endDate",endDate);
		}
		
		if(StringUtils.hasText(staffId)) {
			jpql.append(" and s.id =:staffId ");
			searchParameter.put("staffId",staffId);
		}
		
		if(StringUtils.hasText(nodeId)) {
			jpql.append(" and n.id =:nodeId ");
			searchParameter.put("nodeId",nodeId);
		}
		jpql.append(" order by el.createDate desc ");
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	}
	
	public Pager<EventList> assistEventList(Pager<EventList> pager,String userId,Boolean isFinish){
		StringBuffer jpql = new StringBuffer();
		jpql.append(" select el from EventList el join EventListAssist ea on el.id = ea.eventListId where ea.userId = :userId  ");
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		searchParameter.put("userId", userId);
		if(isFinish!=null) {
			jpql.append(" and ea.isFinish = :isFinish ");
			searchParameter.put("isFinish", isFinish);
		}else {
			jpql.append(" and ea.isFinish is null ");
		}
		jpql.append(" order by ea.createDate desc ");
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	}

	@Override
	public Pager<EventList> historyEventList(Pager<EventList> pager,Date eventStartDate, Date eventEndDate, String event,Integer warningLevel,String node,String nodeType,String mapName,String buildName,String siteName,String operatorId,String status , String employeeId
			,Boolean isOrder,String sortBy,Boolean isSpecial,Boolean isUrgent) {
		StringBuffer jpql = new StringBuffer();
		Map<String,String> joinKey = new HashMap<String, String>();
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		jpql.append(" select distinct el from EventList el ");
		jpql.append(" left join Node n on el.sourceCode = n.sourceCode ");
		jpql.append(" left join NodeType nt on n.nodeTypeId = nt.id ");
		jpql.append(" left join Map m on n.mapId = m.id ");
		jpql.append(" left join BuildingInfo bi on m.buildId = bi.id ");
		jpql.append(" left join SiteInfo si on si.id = bi.siteInfoId ");
		jpql.append(" left join AdminUser au on au.id = el.operator ");
		jpql.append(" left join ActionList al on el.id = al.eventListId ");
		
//		if(StringUtils.hasText(node)) {
//			joinKey.put("node", "");
//			jpql.append(" join Node n on el.sourceCode = n.sourceCode ");
//		}
//		if(StringUtils.hasText(nodeType)) {
//			joinKey.put("nodeType", "");
//			if(!joinKey.containsKey("node")) {
//				jpql.append(" join Node n on el.sourceCode = n.sourceCode ");
//			}
//			jpql.append(" join NodeType nt on n.nodeTypeId = nt.id ");
//		}
//		if(StringUtils.hasText(mapName)) {
//			joinKey.put("map", "");
//			if(!joinKey.containsKey("node")) {
//				jpql.append(" join Node n on el.sourceCode = n.sourceCode ");
//			}
//			jpql.append(" join Map m on n.mapId = m.id ");
//		}
//		
//		if(StringUtils.hasText(buildName)) {
//			joinKey.put("build", "");
//			if(!joinKey.containsKey("node")) {
//				jpql.append(" join Node n on el.sourceCode = n.sourceCode ");
//			}
//			if(!joinKey.containsKey("map")) {
//				jpql.append(" join Map m on n.mapId = m.id ");
//			}
//			jpql.append(" join BuildingInfo bi on m.buildId = bi.id ");
//		}
//		
//		if(StringUtils.hasText(siteName)) {
//			if(!joinKey.containsKey("node")) {
//				jpql.append(" join Node n on el.sourceCode = n.sourceCode ");
//			}
//			if(!joinKey.containsKey("map")) {
//				jpql.append(" join Map m on n.mapId = m.id ");
//			}
//			if(!joinKey.containsKey("build")) {
//				jpql.append(" join BuildingInfo bi on m.buildId = bi.id ");
//			}
//			jpql.append(" join SiteInfo si on si.id = bi.siteInfoId ");
//		}
//		
//		if(StringUtils.hasText(operatorId)) {
//			jpql.append(" join AdminUser au on au.id = el.operator ");
//		}
//		
//		if(StringUtils.hasText(employeeId)) {
//			jpql.append(" join ActionList al on el.id = al.eventListId ");
//		}
		
		jpql.append(" where 1=1 ");
		
		
		if(StringUtils.hasText(status)) {
			if(status.split(",").length==2) {
				jpql.append(" and (el.status = :status1 or el.status = :status2) ");
				searchParameter.put("status1", status.split(",")[0]);
				searchParameter.put("status2", status.split(",")[1]);
			}else {
				jpql.append(" and el.status = :status  ");
				searchParameter.put("status", status);
			}
		}
		if(StringUtils.hasText(event)) {
			jpql.append(" and ( el.id like :event or  el.eventName like :event ) ");
			searchParameter.put("event", "%"+event+"%");
		}
		if(warningLevel!=null) {
			jpql.append(" and el.warningLevel = :warningLevel ");
			searchParameter.put("warningLevel", warningLevel);
		}
		if(StringUtils.hasText(node)) {
			jpql.append(" and ( n.code like :node or n.name like :node or n.sourceCode like :node or n.source like :node ) ");
			searchParameter.put("node", "%"+node+"%");
		}
		if(StringUtils.hasText(nodeType)) {
			jpql.append(" and nt.id = :nodeType ");
			searchParameter.put("nodeType", nodeType);
		}
		if(StringUtils.hasText(mapName)) {
			jpql.append(" and m.name like :mapName ");
			searchParameter.put("mapName", "%"+mapName+"%");
		}
		if(StringUtils.hasText(buildName)) {
			jpql.append(" and bi.name like :buildName ");
			searchParameter.put("buildName", "%"+buildName+"%");
		}
		if(StringUtils.hasText(siteName)) {
			jpql.append(" and si.name like :siteName ");
			searchParameter.put("siteName", "%"+siteName+"%");
		}
		
		if(StringUtils.hasText(operatorId)) {
			jpql.append(" and au.id = :operator ");
			searchParameter.put("operator", operatorId);
		}
		
		if(StringUtils.hasText(employeeId)) {
			jpql.append(" and al.operator = :employeeId ");
			searchParameter.put("employeeId", employeeId);
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(eventStartDate!=null) {
			jpql.append(" and  el.eventDate >= :eventStartDate ");
			searchParameter.put("eventStartDate", dateFormat.format(eventStartDate));
		}
		
		if(eventEndDate!=null) {
			jpql.append(" and  el.eventDate <= :eventEndDate ");
			searchParameter.put("eventEndDate", dateFormat.format(eventEndDate));
		}
		
		if(java.util.Objects.nonNull(isSpecial)) {
			jpql.append(" and  el.isSpecial = :isSpecial ");
			searchParameter.put("isSpecial", isSpecial);
		}
		
		if(java.util.Objects.nonNull(isUrgent)) {
			jpql.append(" and  el.isUrgent = :isUrgent ");
			searchParameter.put("isUrgent", isUrgent);
		}
		
		
		
		if(StringUtils.hasText(sortBy)) {
			if(sortBy.equals("nodeType")) {
				jpql.append(" order by  nt.name desc ");
			}
			if(sortBy.equals("warningLevel")) {
				jpql.append(" order by  el.warningLevel desc ");
			}
			if(sortBy.equals("eventDate")) {
				jpql.append(" order by  el.eventDate desc ");
			}
			if(sortBy.equals("eventName")) {
				jpql.append(" order by  el.eventName desc ");
			}
			if(sortBy.equals("nodeName")) {
				jpql.append(" order by  n.name desc ");
			}
			if(sortBy.equals("isSpecial")) {
				jpql.append(" order by  el.isSpecial desc ");
			}
			if(sortBy.equals("operator")) {
				jpql.append(" order by  el.operator desc ");
			}
			if(sortBy.equals("currentHandleUser")) {
				jpql.append(" order by  el.currentHandleUser desc ");
			}
			if(sortBy.equals("status")) {
				jpql.append(" order by  el.status desc ");
			}
			if(sortBy.equals("mapName")) {
				jpql.append(" order by  m.name desc ");
			}
		}else {
			jpql.append(" order by   el.createDate desc ");
		}
		
		
		
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	} 
	

}
