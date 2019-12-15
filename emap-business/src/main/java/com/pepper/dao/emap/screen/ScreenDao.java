package com.pepper.dao.emap.screen;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.screen.Screen;

public interface ScreenDao extends BaseDao<Screen> {

<<<<<<< HEAD
	public Screen findOneByCode(String code);
=======
	public Screen findFirstByCode(String code);
>>>>>>> refs/heads/master
}
