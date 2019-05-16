package com.pepper.dao.emap.site;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.site.SiteInfo;

public interface SiteInfoDao  extends BaseDao<SiteInfo> {

	/**
	 * 根据code查询 城区
	 * @param code
	 * @return
	 */
	public SiteInfo findByCode(String code);
}
