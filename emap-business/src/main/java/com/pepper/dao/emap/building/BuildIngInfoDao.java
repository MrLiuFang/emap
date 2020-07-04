package com.pepper.dao.emap.building;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.building.BuildingInfo;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BuildIngInfoDao extends BaseDao<BuildingInfo> ,BuildIngInfoDaoEx{
	
	/**
	 * 根据Code查询建筑
	 * @param code
	 * @return
	 */
	public BuildingInfo findFirstByCode(String code);
	
	
	public BuildingInfo findFirstByName(String name);


}
