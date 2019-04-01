package com.pepper.service.emap.map;

import java.util.List;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.map.MapImageUrl;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface MapImageUrlService extends BaseService<MapImageUrl> {

	/**
	 * 
	 * @param mapId
	 */
	public void deleteByMapId(String mapId);
	
	/**
	 * 
	 * @param mapId
	 * @return
	 */
	public List<MapImageUrl> findByMapId(String mapId);
}
