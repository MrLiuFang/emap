package com.pepper.service.emap.event.impl;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.event.EventRuleDao;
import com.pepper.model.emap.event.EventRule;
import com.pepper.service.emap.event.EventRuleService;

@Service(interfaceClass = EventRuleService.class)
public class EventRuleServiceImpl extends BaseServiceImpl<EventRule> implements EventRuleService {

	@Resource
	private EventRuleDao eventRuleDao;
	
	@Override
	public EventRule findByNodeId(String nodeId) {
<<<<<<< HEAD
		return eventRuleDao.findOneByNodeId(nodeId);
=======
		return eventRuleDao.findFirstByNodeId(nodeId);
>>>>>>> refs/heads/master
	}

	@Override
	public EventRule findByNodeTypeId(String nodeTypeId) {
<<<<<<< HEAD
		return eventRuleDao.findOneByNodeTypeId(nodeTypeId);
=======
		return eventRuleDao.findFirstByNodeTypeId(nodeTypeId);
>>>>>>> refs/heads/master
	}

}
