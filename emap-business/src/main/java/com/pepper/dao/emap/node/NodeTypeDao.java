package com.pepper.dao.emap.node;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.node.NodeType;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface NodeTypeDao extends BaseDao<NodeType> {

<<<<<<< HEAD
	public NodeType findOneByCode(String code);
=======
	public NodeType findFirstByCode(String code);
>>>>>>> refs/heads/master
	
<<<<<<< HEAD
	public NodeType findOneByName(String name);
=======
	public NodeType findFirstByName(String name);
>>>>>>> refs/heads/master
}
