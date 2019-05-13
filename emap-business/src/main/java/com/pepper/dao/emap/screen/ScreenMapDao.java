package com.pepper.dao.emap.screen;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.screen.ScreenMap;

public interface ScreenMapDao extends BaseDao<ScreenMap> {

	public List<ScreenMap> findByScreenId(String screenId);
	
	@Transactional(propagation=Propagation.REQUIRED)
    public void deleteByScreenId(String screenId);
}
