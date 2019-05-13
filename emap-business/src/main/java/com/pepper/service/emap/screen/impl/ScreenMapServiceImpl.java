package com.pepper.service.emap.screen.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.screen.ScreenMapDao;
import com.pepper.model.emap.screen.ScreenMap;
import com.pepper.service.emap.screen.ScreenMapService;

@Service(interfaceClass=ScreenMapService.class)
public class ScreenMapServiceImpl extends BaseServiceImpl<ScreenMap> implements ScreenMapService {
	
	@Resource
	private ScreenMapDao screenMapDao;

	@Override
	public List<ScreenMap> findByScreenId(String screenId) {
		return screenMapDao.findByScreenId(screenId);
	}

	@Override
	public void deleteByScreenId(String screenId) {
		screenMapDao.deleteByScreenId(screenId);
	}

	
}
