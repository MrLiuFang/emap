package com.pepper.service.emap.site.impl;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.site.SiteInfoDao;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.service.emap.site.SiteInfoService;

@Service(interfaceClass=SiteInfoService.class)
public class SiteInfoServiceImpl extends BaseServiceImpl<SiteInfo> implements SiteInfoService {
	
	@Resource
	private SiteInfoDao siteInfoDao;

	@Override
	public SiteInfo findByCode(String code) {
		return siteInfoDao.findByCode(code);
	}

}
