package com.pepper.model.emap.vo;

import java.util.List;

import com.pepper.model.emap.map.Map;
import com.pepper.model.emap.map.MapImageUrl;

public class MapVo extends Map {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3480502365837337896L;
	
	private BuildingInfoVo build;
	
	private List<MapImageUrl> mapImageUrl;

	public BuildingInfoVo getBuild() {
		return build;
	}

	public void setBuild(BuildingInfoVo build) {
		this.build = build;
	}

	public List<MapImageUrl> getMapImageUrl() {
		return mapImageUrl;
	}

	public void setMapImageUrl(List<MapImageUrl> mapImageUrl) {
		this.mapImageUrl = mapImageUrl;
	}
	
}
