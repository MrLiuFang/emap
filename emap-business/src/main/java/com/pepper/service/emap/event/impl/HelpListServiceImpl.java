package com.pepper.service.emap.event.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.event.HelpListDao;
import com.pepper.model.emap.event.HelpList;
import com.pepper.service.emap.event.HelpListService;

@Service(interfaceClass=HelpListService.class)
public class HelpListServiceImpl extends BaseServiceImpl<HelpList> implements HelpListService {

	@Resource
	private HelpListDao helpListDao;

	@Override
	public List<HelpList> findByNodeTypeId(String nodeTypeId) {
		return helpListDao.findByNodeTypeId(nodeTypeId);
	}

	@Override
	public List<HelpList> findByNodeTypeIdAndIdIn(String nodeTypeId, String... id) {
		return helpListDao.findByNodeTypeIdAndIdIn(nodeTypeId, id);
	}
}
