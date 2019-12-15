package com.pepper.dao.emap.subsystem;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.subsystem.Subsystem;

public interface SubsystemDao  extends BaseDao<Subsystem> {

	public Subsystem findFirstByCode(String code);
}
