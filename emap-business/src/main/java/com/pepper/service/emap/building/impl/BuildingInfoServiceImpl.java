package com.pepper.service.emap.building.impl;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.building.BuildIngInfoDao;
import com.pepper.model.emap.building.BuildingInfo;
import com.pepper.service.emap.building.BuildingInfoService;

@Service(interfaceClass=BuildingInfoService.class )
public class BuildingInfoServiceImpl extends BaseServiceImpl<BuildingInfo> implements BuildingInfoService {

	@Resource
	private BuildIngInfoDao buildIngInfoDao;

	@Override
	public BuildingInfo findByCode(String code) {
		return buildIngInfoDao.findFirstByCode(code);
	}

	@Override
	public BuildingInfo findByName(String name) {
		return buildIngInfoDao.findFirstByName(name);
	}

}
