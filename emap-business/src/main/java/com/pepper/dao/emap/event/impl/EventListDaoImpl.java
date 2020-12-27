package com.pepper.dao.emap.event.impl;

import java.text.SimpleDateFormat;
import java.util.*;

import com.pepper.core.constant.SearchConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

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

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Pager<EventList> List(Pager<EventList> pager, Boolean isUrgent) {
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		searchParameter.put("sourceCode",pager.getJpqlParameter().getSearchParameter().get(SearchConstant.IN+"_sourceCode"));
		jpql.append(" from EventList t1 where t1.id in ( ");
		jpql.append("  select el.id from  EventList el where el.status = 'N'   AND el.warningLevel  " ).append(isUrgent?">=":"<");
		jpql.append(" (select distinct er.warningLevel from EventRule er join Node n on er.nodeId=n.id where el.sourceCode=n.sourceCode and t1.id=el.id ) ) ");
		jpql.append( " or id in ( ");
		jpql.append(" select el.id from  EventList el where el.status = 'N'   AND el.warningLevel  " ).append(isUrgent?">=":"<");
		jpql.append(" (select distinct er.warningLevel from EventRule er join Node n on n.nodeTypeId = er.nodeTypeId where el.sourceCode=n.sourceCode and t1.id=el.id )   ");
		jpql.append(" and el.id not in (select t2.id from EventList t2 join Node t3 on t2.sourceCode = t3.sourceCode join EventRule t4 on t3.id = t4.nodeId where t2.status = 'N'  )   ");
		jpql.append(" ) and t1.sourceCode in (:sourceCode) order by createDate desc   ");
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	}
	
	@Override
	public Pager<EventList> appList(Pager<EventList> pager, Boolean isFinish,Boolean isUrgent,String eventId,String nodeName,String eventName,Date startDate,Date endDate,String currentHandleUser){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ; 
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		jpql.append(" select t1 from EventList t1 join Node t2 on t1.sourceCode = t2.sourceCode ");
		if(java.util.Objects.nonNull(isFinish) && isFinish ) {
			jpql.append("  join ActionList t3 on t1.id = t3.eventListId ");
		}
		jpql.append("  where 1=1  ");
		if(java.util.Objects.nonNull(isFinish) &&isFinish ) {
			jpql.append(" and t3.operator = :operator  ");
			searchParameter.put("operator", currentHandleUser);
			
			jpql.append(" and t3.status = 'B'  ");
		}else if(java.util.Objects.nonNull(isFinish) && !isFinish ) {
			jpql.append(" and t1.status not in ('P','B')  ");
			jpql.append(" and t1.currentHandleUser = :currentHandleUser  ");
			searchParameter.put("currentHandleUser", currentHandleUser);
		}
		
		if(java.util.Objects.nonNull(isUrgent)) {
			if(isUrgent) {
				jpql.append(" and t1.isUrgent is true  ");
			}else {
				jpql.append(" and t1.isUrgent is not true ");
			}
		}
		if(StringUtils.hasText(eventId)) {
			jpql.append(" and t1.eventId like :eventId ");
			searchParameter.put("eventId", "%"+eventId+"%");
		}
		if(StringUtils.hasText(eventName)) {
			jpql.append(" and t1.eventName like :eventName  ");
			searchParameter.put("eventName", "%"+eventName+"%");
		}
		if(StringUtils.hasText(nodeName)) {
			jpql.append(" and t2.name like :nodeName  ");
			searchParameter.put("nodeName", "%"+nodeName+"%");
		}
		if(java.util.Objects.nonNull(startDate)) {
			jpql.append(" and t1.eventDate >= :startDate  ");
			searchParameter.put("startDate", dateFormat.format(startDate));
		}
		if(java.util.Objects.nonNull(endDate)) {
			jpql.append(" and t1.eventDate <= :endDate  ");
			searchParameter.put("endDate", dateFormat.format(endDate));
		}
		
		
		jpql.append(" order by t1.assignDate desc  ");
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	}

	@Override
	public Pager<EventList> transferList(Pager<EventList> pager,String dispatchFrom,String eventId,Boolean isUrgent,String nodeName,String eventName,Date startDate,Date endDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ; 
		StringBuffer jpql = new StringBuffer();
		jpql.append("select  distinct el from  EventList el join EventDispatch ed on el.id = ed.eventListId join Node n on el.sourceCode = n.sourceCode  "
				+ " where ed.dispatchFrom = :dispatchFrom and ed.isEmployeeTransfer = true  ");
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		searchParameter.put("dispatchFrom", dispatchFrom);
		if(StringUtils.hasText(nodeName)) {
			jpql.append(" and n.name like :nodeName  ");
			searchParameter.put("nodeName", "%"+nodeName+"%");
		}
		
		if(StringUtils.hasText(eventId)) {
			jpql.append(" and el.eventId like :eventId ");
			searchParameter.put("eventId", "%"+eventId+"%");
		}
		if(java.util.Objects.nonNull(isUrgent)) {
			if(isUrgent) {
				jpql.append(" and el.isUrgent is true  ");
			}else {
				jpql.append(" and el.isUrgent is not true ");
			}
		}
		if(StringUtils.hasText(eventName)) {
			jpql.append(" and el.eventName like :eventName  ");
			searchParameter.put("eventName", "%"+eventName+"%");
		}
		if(java.util.Objects.nonNull(startDate)) {
			jpql.append(" and el.eventDate >= :startDate  ");
			searchParameter.put("startDate", dateFormat.format(startDate));
		}
		if(java.util.Objects.nonNull(endDate)) {
			jpql.append(" and el.eventDate <= :endDate  ");
			searchParameter.put("endDate", dateFormat.format(endDate));
		}
		jpql.append( " order by ed.createDate desc ");
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
	public Pager<EventList> historyEventList(Pager<EventList> pager, Date eventStartDate, Date eventEndDate, String event, Integer startWarningLevel, Integer endWarningLevel, String node, String nodeType, String mapName, String buildName, String siteName, String operatorId, String status, String employeeId
            , Boolean isOrder, String sortBy, Boolean isSpecial, Boolean isUrgent, Boolean isRoutine, String eventId, String departmentId) {
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
		jpql.append(" left join EventDispatch ed on el.id = ed.eventListId ");
		
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
		
		if(StringUtils.hasText(departmentId)) {
			jpql.append(" and (al.departmentId = :departmentId or ed.departmentId =:departmentId) ");
			searchParameter.put("departmentId", departmentId);
		}
		
		if(StringUtils.hasText(status)) {
			if(status.split(",").length==2) {
				jpql.append(" and (el.status = :status1 or el.status = :status2) ");
				searchParameter.put("status1", status.split(",")[0]);
				searchParameter.put("status2", status.split(",")[1]);
			}else if(java.util.Objects.equals(status.toUpperCase(), "NULL")) {
				jpql.append(" and (el.status is null or el.status = 'N') ");
			}else {
				jpql.append(" and el.status = :status  ");
				searchParameter.put("status", status);
			}
		}
		if(StringUtils.hasText(eventId)) {
			jpql.append(" and el.eventId like :eventId ");
			searchParameter.put("eventId", "%"+eventId+"%");
		}
		if(StringUtils.hasText(event)) {
			jpql.append(" and ( el.id like :event or  el.eventName like :event ) ");
			searchParameter.put("event", "%"+event+"%");
		}
		if(startWarningLevel !=null) {
			jpql.append(" and el.warningLevel >= :startWarningLevel ");
			searchParameter.put("startWarningLevel", startWarningLevel);
		}
		if(endWarningLevel !=null) {
			jpql.append(" and el.warningLevel <= :endWarningLevel ");
			searchParameter.put("endWarningLevel", endWarningLevel);
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

		if (Objects.equals(isRoutine,true)){
			jpql.append(" and  el.isSpecial is not true and el.isUrgent is not true and el.status is not null ");
		}else if (Objects.equals(isRoutine,false)){
			jpql.append(" and ( el.isSpecial is not false or el.isUrgent is not false )  ");
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

	@Override
	public EventList findFirstByNodeId(String nodeId) {
		String jpql = " SELECT t1 FROM EventList t1 JOIN Node t2 ON t1.sourceCode = t2.sourceCode WHERE t2.id = '"+nodeId+"' ORDER BY t1.createDate DESC  ";
		return baseDao.findOne(jpql);
	}

	@Override
	public List<Map<String,Object>> yearTypeCount(String where,Date startDate,Date endDate) {
		List<Object> pararms = new ArrayList<Object>();
		String sql = "select date_format( create_date, '%Y-%m' ) AS create_date,  count(1) as count from t_event_list where 1=1    ";
		if(Objects.equals(where,"isSpecial")){
			sql += " and is_special is true and is_urgent is  not true  ";
		}
		if(Objects.equals(where,"isUrgent")){
			sql += " and is_urgent is true and is_special is  not true ";
		}
		if(Objects.equals(where,"ordinary")){
			sql += " and is_urgent is  not true  and is_special is  not true  ";
		}
		if(Objects.nonNull(startDate)){
			sql += " and create_date >= ? ";
			pararms.add(startDate);
		}
		if(Objects.nonNull(endDate)){
			sql += " and create_date <= ? ";
			pararms.add(endDate);
		}
		sql += " group by date_format( create_date, '%Y-%m' ) ";

		return this.jdbcTemplate.queryForList(sql,pararms.toArray());
	}

	public List<String> userNode(String userId){
		StringBuffer jqpl = new StringBuffer();
		jqpl.append(" select distinct new map(t1.sourceCode as source_code) from Node t1 join Map t2 on t1.mapId = t2.id ");
		jqpl.append(" join BuildingInfo t3 on t2.buildId = t3.id ");
		jqpl.append(" join GroupBuild t4 on t3.id = t4.buildId ");
		jqpl.append(" join GroupUser t5 on t4.groupId = t5.groupId ");
		jqpl.append(" where t5.userId = :userId  ");
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		searchParameter.put("userId",userId);
		List<Map<String,Object>> list = this.baseDao.findToMap(jqpl.toString(),searchParameter);
		List<String> renturList = new ArrayList<String>();
		for(Map<String,Object> map : list){
			renturList.add(String.valueOf(map.get("source_code")));
		}
		return renturList;
	}
}
