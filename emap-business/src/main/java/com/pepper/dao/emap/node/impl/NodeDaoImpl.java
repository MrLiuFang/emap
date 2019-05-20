package com.pepper.dao.emap.node.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseDao;
import com.pepper.core.base.curd.DaoExImpl;
import com.pepper.dao.emap.node.NodeDaoEx;
import com.pepper.model.emap.node.Node;

/**
 * 
 * @author Mr.Liu
 *
 */
public class NodeDaoImpl extends DaoExImpl<Node> implements NodeDaoEx<Node> {

	@Override
	public Pager<Node> findNavigator(Pager<Node> pager,Map<String, Object> parameter) {
		BaseDao<Node>  baseDao = this.getPepperSimpleJpaRepository(this.getClass());
		StringBuffer jpql = new StringBuffer("from Node where id =:id");
		parameter = new HashMap<String, Object>();
		parameter.put("id", "value");
		pager = baseDao.findNavigator(pager, jpql.toString(), parameter);
		return pager;
	}

	@Override
	public Pager<Node> findNavigator(Pager<Node> pager, String code, String name, String source, String sourceCode,
			String mapId, String nodeTypeId, String siteId, String buildId, String floor) {
		StringBuffer jpql = new StringBuffer(" select n from Node n join Map m on n.mapId = m.id join BuildingInfo b on m.buildId = b.id join SiteInfo s on s.id = b.siteInfoId where 1=1 ");
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
		
		jpql.append(" and n.x is not null and n.x <> ''  and n.y is not null and n.y <> '' ");
		
		BaseDao<Node>  baseDao = this.getPepperSimpleJpaRepository(this.getClass());
		return baseDao.findNavigator(pager, jpql.toString(),searchParameter );
	}

	
}
