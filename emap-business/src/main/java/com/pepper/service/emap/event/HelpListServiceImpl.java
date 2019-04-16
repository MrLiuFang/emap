package com.pepper.service.emap.event;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.event.HelpListDao;
import com.pepper.model.emap.event.HelpList;

@Service(interfaceClass=HelpListService.class)
public class HelpListServiceImpl extends BaseServiceImpl<HelpList> implements HelpListService {

	@Resource
	private HelpListDao helpListDao;
}
