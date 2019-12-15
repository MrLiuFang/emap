package com.pepper.dao.emap.node;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.node.NodeType;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface NodeTypeDao extends BaseDao<NodeType> {

	public NodeType findFirstByCode(String code);
	
	public NodeType findFirstByName(String name);
}
