package com.pepper.model.emap.site;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_site_info")
@DynamicUpdate(true)
public class SiteInfo extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6891029094526627282L;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "code")
	private String code;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	

}
