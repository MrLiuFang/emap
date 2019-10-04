package com.pepper.model.emap.vo;

import java.util.List;

import com.pepper.model.emap.building.BuildingInfo;
import com.pepper.model.emap.screen.Screen;
import com.pepper.model.emap.site.SiteInfo;

public class ScreenVo extends Screen {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1190884333782492999L;
	
	private BuildingInfo build;
	
	private SiteInfo site;
	
	private List<ScreenMapVo> map;

	public BuildingInfo getBuild() {
		return build;
	}

	public void setBuild(BuildingInfo build) {
		this.build = build;
	}

	public SiteInfo getSite() {
		return site;
	}

	public void setSite(SiteInfo site) {
		this.site = site;
	}

	public List<ScreenMapVo> getMap() {
		return map;
	}

	public void setMap(List<ScreenMapVo> map) {
		this.map = map;
	}
	
	
}
