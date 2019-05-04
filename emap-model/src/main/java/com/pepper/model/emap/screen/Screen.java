package com.pepper.model.emap.screen;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_screen")
@DynamicUpdate(true)
public class Screen extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6903635020510199117L;
	
	private String buildingId;
	
	private String siteId;
	
	private String mapId;
	
	private Integer sort;

	public String getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}
	
	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	

}
