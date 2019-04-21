package com.pepper.service.emap.event;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.event.EventRule;

public interface EventRuleService extends BaseService<EventRule> {

	/**
	 * 
	 * @param nodeId
	 * @return
	 */
	public EventRule findByNodeId(String nodeId);
}
