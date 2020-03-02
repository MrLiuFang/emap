package com.pepper.model.emap.screen;

import javax.persistence.Column;
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
	
	
	@Column(name = "building_id")
	private String buildingId;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "site_id")
	private String siteId;
	
	@Column(name = "refresh_frequency")
	private Integer refreshFrequency;

	@Column(name = "level")
	private Integer level;

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

	public Integer getRefreshFrequency() {
		return refreshFrequency;
	}

	public void setRefreshFrequency(Integer refreshFrequency) {
		this.refreshFrequency = refreshFrequency;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
}
