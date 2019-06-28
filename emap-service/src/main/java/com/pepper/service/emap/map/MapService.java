package com.pepper.service.emap.map;

import java.util.List;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseService;
import com.pepper.model.emap.map.Map;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface MapService extends BaseService<Map>{
	/**
	 * 根据code查询地图
	 * @param code
	 * @return
	 */
	public Map findByCode(String code);
	
	public List<Map> findByName(String name);
	
	public Pager<Map> findNavigator(Pager<Map> pager, String code, String name, String areaCode, String areaName,
			String buildId, String keyWord, String siteId);
}
