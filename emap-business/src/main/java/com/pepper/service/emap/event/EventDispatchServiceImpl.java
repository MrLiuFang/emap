package com.pepper.service.emap.event;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.model.emap.event.EventDispatch;

@Service(interfaceClass =EventDispatchService.class)
public class EventDispatchServiceImpl extends BaseServiceImpl<EventDispatch> implements EventDispatchService {

}
