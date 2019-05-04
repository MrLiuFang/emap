package com.pepper.model.emap.screen;

import com.pepper.model.emap.building.BuildingInfo;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.vo.MapVo;

public class ScreenVo extends Screen {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1190884333782492999L;
	
	private BuildingInfo build;
	
	private SiteInfo site;
	
	private MapVo map;

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

	public MapVo getMap() {
		return map;
	}

	public void setMap(MapVo map) {
		this.map = map;
	}
	
	

}
