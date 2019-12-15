package com.pepper.model.emap.vo;

import java.util.List;

import com.pepper.model.emap.map.MapImageUrl;
import com.pepper.model.emap.screen.ScreenMap;

public class ScreenMapVo extends ScreenMap {
	/**
		 * 
		 */
	private static final long serialVersionUID = 3481075478346789673L;

//	private BuildingInfoVo build;
	
	private MapVo map;


	public MapVo getMap() {
		return map;
	}

	public void setMap(MapVo map) {
		this.map = map;
	}
	
	
}
