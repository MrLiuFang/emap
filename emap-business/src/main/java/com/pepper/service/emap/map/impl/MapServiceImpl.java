package com.pepper.service.emap.map.impl;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.map.MapDao;
import com.pepper.model.emap.map.Map;
import com.pepper.service.emap.map.MapService;

@Service(interfaceClass=MapService.class)
public class MapServiceImpl extends BaseServiceImpl<Map> implements MapService {
	
	@Resource
	private MapDao mapDao;

	@Override
	public Map findByCode(String code) {
		return mapDao.findByCode(code);
	}
	
}
