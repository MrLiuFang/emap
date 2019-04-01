package com.pepper.model.emap.map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_map_image_url")
@DynamicUpdate(true)
public class MapImageUrl extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -264496489362510062L;
	
	@Column(name = "map_id")
	private String mapId;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "url",length= 1000)
	private String url;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	
	

}
