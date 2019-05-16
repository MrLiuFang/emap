package com.pepper.dao.emap.map;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.map.Map;

public interface MapDao extends BaseDao<Map> {

	/**
	 * 根据code查询地图
	 * @param code
	 * @return
	 */
	public Map findByCode(String code);
}
