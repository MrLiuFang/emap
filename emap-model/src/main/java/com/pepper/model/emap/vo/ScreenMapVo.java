package com.pepper.model.emap.vo;

import java.util.List;

import com.pepper.model.emap.map.MapImageUrl;
import com.pepper.model.emap.screen.ScreenMap;

public class ScreenMapVo extends ScreenMap {
	/**
		 * 
		 */
	private static final long serialVersionUID = 3481075478346789673L;

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
