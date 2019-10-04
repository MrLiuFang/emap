package com.pepper.service.emap.screen;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.screen.Screen;

public interface ScreenService extends BaseService<Screen> {
	
	
	public Screen findByCode(String code);

}
