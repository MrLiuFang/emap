package com.pepper.model.emap.screen;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_screen_map")
@DynamicUpdate(true)
public class ScreenMap extends BaseModel {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7252980146297351728L;

	@Column(name = "screen_id",nullable=false)
	private String screenId;

	@Column(name = "map_id",nullable=false)
	private String mapId;
	
	@Column(name = "refresh_frequency")
	private Integer refreshFrequency;

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getScreenId() {
		return screenId;
	}

	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

	public Integer getRefreshFrequency() {
		return refreshFrequency;
	}

	public void setRefreshFrequency(Integer refreshFrequency) {
		this.refreshFrequency = refreshFrequency;
	}
	
}
