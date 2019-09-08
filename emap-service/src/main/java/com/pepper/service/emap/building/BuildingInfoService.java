package com.pepper.service.emap.building;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.building.BuildingInfo;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface BuildingInfoService extends BaseService<BuildingInfo>{
	
	/**
	 * 根据Code查询建筑
	 * @param code
	 * @return
	 */
	public BuildingInfo findByCode(String code);
	
	public BuildingInfo findByName(String name);

}
