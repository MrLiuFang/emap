package com.pepper.dao.emap.map.impl;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseDao;
import com.pepper.dao.emap.map.MapDaoEx;
import com.pepper.model.emap.map.Map;

public class MapDaoImpl implements MapDaoEx {
	
	@Autowired
	private BaseDao<Map> baseDao;

	@Override
	public Pager<Map> findNavigator(Pager<Map> pager, String code, String name, String areaCode, String areaName,
			String buildId, String keyWord, String siteId) {
		StringBuffer jpql = new StringBuffer();
		java.util.Map<String,Object> searchParameter = new HashMap<String, Object>();
		jpql.append("select m from Map m ");
		
		if(StringUtils.hasText(siteId)) {
			jpql.append(" join BuildingInfo bi on m.buildId = bi.id join SiteInfo si on bi.siteInfoId = si.id ");
			jpql.append(" where si.id = :siteId ");
			searchParameter.put("siteId", siteId);
		}else {
			jpql.append(" where 1 = 1 ");
		}
		
		if(StringUtils.hasText(code)) {
			jpql.append(" and m.code like :code ");
			searchParameter.put("code", "%"+code+"%");
		}
		
		if(StringUtils.hasText(name)) {
			jpql.append(" and m.name like :name ");
			searchParameter.put("name", "%"+name+"%");
		}
		
		if(StringUtils.hasText(areaCode)) {
			jpql.append(" and m.areaCode like :areaCode ");
			searchParameter.put("areaCode", "%"+areaCode+"%");
		}
		
		if(StringUtils.hasText(areaName)) {
			jpql.append(" and m.areaName like :areaName ");
			searchParameter.put("areaName", "%"+areaName+"%");
		}
		
		if(StringUtils.hasText(buildId)) {
			jpql.append(" and m.buildId = :buildId ");
			searchParameter.put("buildId", buildId);
		}
		
		if(StringUtils.hasText(keyWord)) {
			jpql.append(" and ( m.code like :keyWord or m.name like :keyWord or m.areaCode like :keyWord or m.areaName like :keyWord ) ");
			searchParameter.put("keyWord", keyWord);
		}
		
		return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
	}

}
