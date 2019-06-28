package com.pepper.dao.emap.map;

import com.pepper.core.Pager;
import com.pepper.model.emap.map.Map;

public interface MapDaoEx {

	public Pager<Map> findNavigator(Pager<Map> pager,String code,String name,String areaCode,String areaName,String buildId,String keyWord,String siteId);
}
