package com.pepper.dao.emap.event.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.StringUtils;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseDao;
import com.pepper.core.base.curd.DaoExImpl;
import com.pepper.dao.emap.event.EventListDaoEx;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.HelpList;

/**
 * 
 * @author Mr.Liu
 *
 */
public class EventListDaoImpl  extends DaoExImpl<EventList> implements EventListDaoEx {

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
		
		BaseDao<EventList> baseDao =  this.getPepperSimpleJpaRepository(this.getClass());
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
		
	}

	@Override
	public Pager<EventList> transferList(Pager<EventList> pager,String dispatchFrom) {
		BaseDao<EventList> baseDao =  this.getPepperSimpleJpaRepository(this.getClass());
		StringBuffer jpql = new StringBuffer();
		jpql.append("select  distinct el from  EventList el join EventDispatch ed on el.id = ed.eventListId where ed.dispatchFrom = :dispatchFrom ");
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		searchParameter.put("dispatchFrom", dispatchFrom);
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	}


	public Pager<EventList> doorAttendance(Pager<EventList> pager,String eventListId,String nodeId,Date startDate,Date endDate,String staffId){
		BaseDao<EventList> baseDao =  this.getPepperSimpleJpaRepository(this.getClass());
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
		BaseDao<EventList> baseDao =  this.getPepperSimpleJpaRepository(this.getClass());
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
	public Pager<EventList> historyEventList(Pager<EventList> pager, String event, Integer warningLevel, String node,
			String nodeType, String mapName, String buildName, String stieName, String operator, String status) {
		StringBuffer jpql = new StringBuffer();
		Map<String,String> joinKey = new HashMap<String, String>();
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		jpql.append(" select el from EventList el ");
		if(StringUtils.hasText(node)) {
			joinKey.put("node", "");
			jpql.append(" join Node n on el.sourceCode = n.sourceCode ");
		}
		if(StringUtils.hasText(nodeType)) {
			joinKey.put("nodeType", "");
			if(!joinKey.containsKey("node")) {
				jpql.append(" join Node n on el.sourceCode = n.sourceCode ");
			}
			jpql.append(" join NodeType nt on n.nodeTypeId = nt.id ");
		}
		if(StringUtils.hasText(mapName)) {
			joinKey.put("map", "");
			if(!joinKey.containsKey("node")) {
				jpql.append(" join Node n on el.sourceCode = n.sourceCode ");
			}
			jpql.append(" join Map m on n.mapId = m.id ");
		}
		
		if(StringUtils.hasText(buildName)) {
			joinKey.put("build", "");
			if(!joinKey.containsKey("node")) {
				jpql.append(" join Node n on el.sourceCode = n.sourceCode ");
			}
			if(!joinKey.containsKey("map")) {
				jpql.append(" join Map m on n.mapId = m.id ");
			}
			jpql.append(" join BuildingInfo bi on m.buildId = bi.id ");
		}
		
		if(StringUtils.hasText(stieName)) {
			if(!joinKey.containsKey("node")) {
				jpql.append(" join Node n on el.sourceCode = n.sourceCode ");
			}
			if(!joinKey.containsKey("map")) {
				jpql.append(" join Map m on n.mapId = m.id ");
			}
			if(!joinKey.containsKey("build")) {
				jpql.append(" join BuildingInfo bi on m.buildId = bi.id ");
			}
			jpql.append(" join SiteInfo si on si.id = bi.siteInfoId ");
		}
		
		if(StringUtils.hasText(operator)) {
			jpql.append(" join AdminUser au on au.id = el.operator ");
		}
		
		jpql.append(" where 1=1 ");
		
		
		if(StringUtils.hasText(status)) {
			jpql.append(" and el.status = :status  ");
			searchParameter.put("status", status);
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
			jpql.append(" and nt.name like :nodeType ");
			searchParameter.put("nodeType", "%"+nodeType+"%");
		}
		if(StringUtils.hasText(mapName)) {
			jpql.append(" and m.name like :mapName ");
			searchParameter.put("mapName", "%"+mapName+"%");
		}
		if(StringUtils.hasText(buildName)) {
			jpql.append(" and bi.name like :mapName ");
			searchParameter.put("mapName", "%"+mapName+"%");
		}
		if(StringUtils.hasText(stieName)) {
			jpql.append(" and si.name like :stieName ");
			searchParameter.put("stieName", "%"+stieName+"%");
		}
		
		if(StringUtils.hasText(operator)) {
			jpql.append(" and au.name like :operator ");
			searchParameter.put("operator", "%"+operator+"%");
		}
		
		
		BaseDao<EventList> baseDao =  this.getPepperSimpleJpaRepository(this.getClass());
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	} 
	

}
