package com.pepper.service.emap.subsystem;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.subsystem.Subsystem;

public interface SubsystemService extends BaseService<Subsystem>  {

	public Subsystem findByCode(String code);
}
