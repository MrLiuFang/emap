package com.pepper.dao.emap.map;

import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.map.Map;

public interface MapDao extends BaseDao<Map> ,MapDaoEx {

	/**
	 * 根据code查询地图
	 * @param code
	 * @return
	 */
<<<<<<< HEAD
	public Map findOneByCode(String code);
=======
	public Map findFirstByCode(String code);
>>>>>>> refs/heads/master
	
	public List<Map> findByName(String name);
}
