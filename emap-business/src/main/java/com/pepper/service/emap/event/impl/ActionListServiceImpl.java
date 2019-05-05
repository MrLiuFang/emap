package com.pepper.service.emap.event.impl;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.model.emap.event.ActionList;
import com.pepper.service.emap.event.ActionListService;

@Service(interfaceClass =ActionListService.class)
public class ActionListServiceImpl extends BaseServiceImpl<ActionList> implements ActionListService {

}
