package com.pepper.model.emap.vo;

import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.staff.Staff;

/**
 * 
 * @author Mr.Liu
 *
 */
public class StaffVo extends Staff {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5188464496531346732L;
	

	private SiteInfo site;


	public SiteInfo getSite() {
		return site;
	}


	public void setSite(SiteInfo site) {
		this.site = site;
	}
	
	
	
}
