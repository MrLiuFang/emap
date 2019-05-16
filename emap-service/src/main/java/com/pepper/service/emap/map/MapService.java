package com.pepper.service.emap.map;

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
}
