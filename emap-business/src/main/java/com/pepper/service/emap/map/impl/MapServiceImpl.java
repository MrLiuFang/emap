package com.pepper.service.emap.map.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.Pager;
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
		return mapDao.findFirstByCode(code);
	}

	@Override
	public List<Map> findByName(String name) {
		return mapDao.findByName(name);
	}

	@Override
	public Pager<Map> findNavigator(Pager<Map> pager, String code, String name, String areaCode, String areaName,
			String buildId, String keyWord, String siteId) {
		return mapDao.findNavigator(pager, code, name, areaCode, areaName, buildId, keyWord, siteId);
	}
	
}
