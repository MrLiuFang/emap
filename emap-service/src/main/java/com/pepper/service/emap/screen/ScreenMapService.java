package com.pepper.service.emap.screen;

import java.util.List;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.screen.ScreenMap;

public interface ScreenMapService extends BaseService<ScreenMap> {

	public List<ScreenMap> findByScreenId(String screenId);
	
	public void deleteByScreenId(String screenId);
}
