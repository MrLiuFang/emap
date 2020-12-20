package com.pepper.service.emap.lift.impl;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.model.emap.lift.Lift;
import com.pepper.service.emap.lift.LiftService;
import org.apache.dubbo.config.annotation.Service;

@Service(interfaceClass = LiftService.class)
public class LiftServiceImpl extends BaseServiceImpl<Lift> implements LiftService {
}
