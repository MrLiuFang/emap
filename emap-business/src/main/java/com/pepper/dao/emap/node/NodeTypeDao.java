package com.pepper.dao.emap.node;

import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.node.NodeType;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface NodeTypeDao extends BaseDao<NodeType> {

	public NodeType findByCode(String code);
	
	public List<NodeType> findByName(String name);
}
