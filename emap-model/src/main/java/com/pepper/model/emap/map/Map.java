package com.pepper.model.emap.map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_map_info")
@DynamicUpdate(true)
public class Map extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2458345648543630612L;

	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;
	
	@Column(name = "area_code")
	private String areaCode;
	
	@Column(name = "area_name")
	private String areaName;
	
	@Column(name = "build_Id")
	private String buildId;
	
	@Column(name = "floor")
	private String floor;
	
	@Column(name = "image_heigh")
	private String imageHeigh;
	
	@Column(name = "image_width")
	private String imageWidth;
	
	@Column(name = "max_level")
	private Integer maxLevel;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getBuildId() {
		return buildId;
	}

	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getImageHeigh() {
		return imageHeigh;
	}

	public void setImageHeigh(String imageHeigh) {
		this.imageHeigh = imageHeigh;
	}

	public String getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(String imageWidth) {
		this.imageWidth = imageWidth;
	}

	public Integer getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(Integer maxLevel) {
		this.maxLevel = maxLevel;
	}
	
	
}
