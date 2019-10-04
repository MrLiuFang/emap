package com.pepper.service.emap.subsystem.impl;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.subsystem.SubsystemDao;
import com.pepper.model.emap.subsystem.Subsystem;
import com.pepper.service.emap.subsystem.SubsystemService;

@Service(interfaceClass =SubsystemService.class )
public class SubsystemServiceImpl  extends BaseServiceImpl<Subsystem> implements SubsystemService {

	@Autowired
	private SubsystemDao subsystemDao;

	@Override
	public Subsystem findByCode(String code) {
		return subsystemDao.findOneByCode(code);
	}
}
