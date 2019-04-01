package com.pepper.service.emap.map.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.map.MapImageUrlDao;
import com.pepper.model.emap.map.MapImageUrl;
import com.pepper.service.emap.map.MapImageUrlService;

@Service(interfaceClass=MapImageUrlService.class)
public class MapImageUrlServiceImpl extends BaseServiceImpl<MapImageUrl> implements MapImageUrlService {

	@Resource
	private MapImageUrlDao mapImageUrlDao;

	@Override
	@Transactional
	public void deleteByMapId(String mapId) {
		mapImageUrlDao.deleteByMapId(mapId);
		
	}

	@Override
	public List<MapImageUrl> findByMapId(String mapId) {
		return mapImageUrlDao.findByMapId(mapId);
	}

}
