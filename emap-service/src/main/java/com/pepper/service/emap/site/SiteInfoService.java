package com.pepper.service.emap.site;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.site.SiteInfo;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface SiteInfoService extends BaseService<SiteInfo> {

	/**
	 * 根据code查询 城区
	 * @param code
	 * @return
	 */
	public SiteInfo findByCode(String code);
}
