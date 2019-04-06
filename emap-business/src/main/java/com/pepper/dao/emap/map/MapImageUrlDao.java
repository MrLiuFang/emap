package com.pepper.dao.emap.map;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.map.MapImageUrl;

public interface MapImageUrlDao extends BaseDao<MapImageUrl>  {

	
	@Query(value="delete from MapImageUrl  where mapId  = ?1  ")
	@Modifying
	public void deleteByMapId(String mapId);
	
	public List<MapImageUrl> findByMapId(String mapId);
}
