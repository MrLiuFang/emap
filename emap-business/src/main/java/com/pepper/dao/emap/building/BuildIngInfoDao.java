package com.pepper.dao.emap.building;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.building.BuildingInfo;

public interface BuildIngInfoDao extends BaseDao<BuildingInfo> {
	
	/**
	 * 根据Code查询建筑
	 * @param code
	 * @return
	 */
	public BuildingInfo findOneByCode(String code);
	
	
	public BuildingInfo findOneByName(String name);
}
