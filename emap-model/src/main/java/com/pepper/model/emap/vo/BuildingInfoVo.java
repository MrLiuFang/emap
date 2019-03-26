package com.pepper.model.emap.vo;

import com.pepper.model.emap.building.BuildingInfo;
import com.pepper.model.emap.site.SiteInfo;

public class BuildingInfoVo extends BuildingInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3846256573757890537L;
	
	private SiteInfo site;

	public SiteInfo getSite() {
		return site;
	}

	public void setSite(SiteInfo site) {
		this.site = site;
	}

	
}
