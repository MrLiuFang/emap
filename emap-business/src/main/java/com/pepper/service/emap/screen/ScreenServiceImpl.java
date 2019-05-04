package com.pepper.service.emap.screen;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.screen.ScreenDao;
import com.pepper.model.emap.screen.Screen;

@Service(interfaceClass=ScreenService.class)
public class ScreenServiceImpl extends BaseServiceImpl<Screen> implements ScreenService {

	@Reference
	private ScreenDao screenDao;

}
