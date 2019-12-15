package com.pepper.service.emap.screen.impl;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.screen.ScreenDao;
import com.pepper.model.emap.screen.Screen;
import com.pepper.service.emap.screen.ScreenService;

@Service(interfaceClass=ScreenService.class)
public class ScreenServiceImpl extends BaseServiceImpl<Screen> implements ScreenService {

	@Resource
	private ScreenDao screenDao;

	@Override
	public Screen findByCode(String code) {
<<<<<<< HEAD
		return screenDao.findOneByCode(code);
=======
		return screenDao.findFirstByCode(code);
>>>>>>> refs/heads/master
	}

}
