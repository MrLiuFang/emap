package com.pepper.dao.emap.report.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseModel;
import com.pepper.core.base.curd.DaoExImpl;
import com.pepper.dao.emap.report.ReportDaoEx;

public class ReportDaoImpl extends DaoExImpl<BaseModel> implements ReportDaoEx {

	@Override
	public Pager<Map<String,Object>> findNodeTypeAndMap(Pager<Map<String,Object>> pager, String nodeTypeId, String mapId) {
		StringBuffer jpql = new StringBuffer();
		jpql.append(" select distinct new map(  t2.id as nodeTypeId ,t2.name as nodeTypeName, t3.id as mapId, t3.name as mapName ) "
				+ "from Node t1 join NodeType t2 on t1.nodeTypeId = t2.id join Map t3 on t1.mapId = t3. id where 1=1 ");
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		if(StringUtils.hasText(nodeTypeId)) {
			jpql.append(" and t2.id = :nodeTypeId ");
			searchParameter.put("nodeTypeId", nodeTypeId);
		}
		if(StringUtils.hasText(mapId)) {
			jpql.append(" and t3.id = :mapId ");
			searchParameter.put("mapId", mapId);
		}
		
		return this.getPepperSimpleJpaRepository(this.getClass()).findNavigatorToMap(pager, jpql.toString(),searchParameter);
	}
	
	public Integer findNodeCout(String nodeTypeId, String mapId) {
		StringBuffer jpql = new StringBuffer();
		jpql.append(" select new map( count(t1.id) as nodeCount ) "
				+ "from Node t1 join NodeType t2 on t1.nodeTypeId = t2.id join Map t3 on t1.mapId = t3. id where 1=1 ");
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		if(StringUtils.hasText(nodeTypeId)) {
			jpql.append(" and t2.id = :nodeTypeId ");
			searchParameter.put("nodeTypeId", nodeTypeId);
		}
		if(StringUtils.hasText(mapId)) {
			jpql.append(" and t3.id = :mapId ");
			searchParameter.put("mapId", mapId);
		}
		
		Map<String, Object> map =this.getPepperSimpleJpaRepository(this.getClass()).findOneToMap(jpql.toString(), searchParameter);
		if(map!=null && map.containsKey("nodeCount")) {
			return Integer.valueOf(map.get("nodeCount").toString());
		}
		return 0;
	}

}
