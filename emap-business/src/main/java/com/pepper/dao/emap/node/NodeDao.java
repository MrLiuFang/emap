package com.pepper.dao.emap.node;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.node.Node;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface NodeDao extends BaseDao<Node> , NodeDaoEx<Node> {

	/**
	 * 
	 * @param sourceCode
	 * @return
	 */
	public Node findBySourceCode(String sourceCode);
}
