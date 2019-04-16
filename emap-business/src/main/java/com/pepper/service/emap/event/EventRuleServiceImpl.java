package com.pepper.service.emap.event;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.model.emap.event.EventRule;

@Service(interfaceClass = EventRuleService.class)
public class EventRuleServiceImpl extends BaseServiceImpl<EventRule> implements EventRuleService {

}
