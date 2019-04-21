package com.pepper.dao.emap.event;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.event.EventRule;

public interface EventRuleDao extends BaseDao<EventRule> {

	/**
	 * 
	 * @param nodeId
	 * @return
	 */
	public EventRule findByNodeId(String nodeId);
}
