package com.pepper.dao.emap.event;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.event.EventRule;

public interface EventRuleDao extends BaseDao<EventRule> {

	/**
	 * 
	 * @param nodeId
	 * @return
	 */
<<<<<<< HEAD
	public EventRule findOneByNodeId(String nodeId);
=======
	public EventRule findFirstByNodeId(String nodeId);
>>>>>>> refs/heads/master
	
	/**
	 * 
	 * @param nodeId
	 * @return
	 */
<<<<<<< HEAD
	public EventRule findOneByNodeTypeId(String nodeTypeId);
=======
	public EventRule findFirstByNodeTypeId(String nodeTypeId);
>>>>>>> refs/heads/master
	

}
