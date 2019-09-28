package com.pepper.dao.emap.node.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseDao;
import com.pepper.dao.emap.node.NodeDaoEx;
import com.pepper.model.emap.node.Node;

/**
 * 
 * @author Mr.Liu
 *
 */
public class NodeDaoImpl implements NodeDaoEx<Node> {

	@Autowired
	private BaseDao<Node> baseDao;
	
	@Override
	public Pager<Node> findNavigator(Pager<Node> pager,Map<String, Object> parameter) {
		StringBuffer jpql = new StringBuffer("from Node where id =:id");
		parameter = new HashMap<String, Object>();
		parameter.put("id", "value");
		pager = baseDao.findNavigator(pager, jpql.toString(), parameter);
		return pager;
	}

	@Override
	public Pager<Node> findNavigator(Pager<Node> pager, String code, String name, String source, String sourceCode,
			String mapId, String nodeTypeId, String siteId, String buildId, String floor,String hasXY,String keyWord) {
		StringBuffer jpql = new StringBuffer(" select n from Node n left join Map m on n.mapId = m.id left join BuildingInfo b on m.buildId = b.id left join SiteInfo s on s.id = b.siteInfoId where 1=1 ");
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		if(StringUtils.hasText(code)) {
			jpql.append(" and n.code  like :code");
			searchParameter.put("code", "%"+code+"%");
		}
		if(StringUtils.hasText(name)) {
			jpql.append(" and n.name  like :name");
			searchParameter.put("name", "%"+name+"%");
		}
		if(StringUtils.hasText(source)) {
			jpql.append(" and n.source  like :source");
			searchParameter.put("source", "%"+source+"%");
		}
		if(StringUtils.hasText(sourceCode)) {
			jpql.append(" and n.sourceCode  like :sourceCode");
			searchParameter.put("sourceCode", "%"+sourceCode+"%");
		}
		if(StringUtils.hasText(mapId)) {
			jpql.append(" and n.mapId = :mapId");
			searchParameter.put("mapId", mapId);
		}
		if(StringUtils.hasText(nodeTypeId)) {
			jpql.append(" and n.nodeTypeId = :nodeTypeId");
			searchParameter.put("nodeTypeId", nodeTypeId);
		}
		if(StringUtils.hasText(buildId)) {
			jpql.append(" and m.buildId = :buildId");
			searchParameter.put("buildId", buildId);
		}
		if(StringUtils.hasText(siteId)) {
			jpql.append(" and s.id = :siteId");
			searchParameter.put("siteId", siteId);
		}
		if(StringUtils.hasText(floor)) {
			jpql.append(" and m.floor like :floor");
			searchParameter.put("floor", "%"+floor+"%");
		}
		if(StringUtils.hasText(hasXY)&&hasXY.toLowerCase().equals("true")) {
			jpql.append(" and n.x is not null and n.x <> ''  and n.y is not null and n.y <> '' ");
		}else if(StringUtils.hasText(hasXY)&&hasXY.toLowerCase().equals("false")) {
			jpql.append(" and ( n.x is null or n.x = ''  or n.y is null or n.y = '' ) ");
		}
		
		if(StringUtils.hasText(keyWord)) {
//			jpql.append(" and ( n.code  like :keyWord or n.name  like :keyWord or n.source  like :keyWord or n.sourceCode  like :keyWord )");
			jpql.append(" and n.name like :keyWord ");
			searchParameter.put("keyWord", "%"+keyWord+"%");
		}
		jpql.append(" order by n.code ");
//		jpql.append(" and n.x is not null and n.x <> ''  and n.y is not null and n.y <> '' ");
		
		return baseDao.findNavigator(pager, jpql.toString(),searchParameter );
	}

	
}
