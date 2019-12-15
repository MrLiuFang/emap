package com.pepper.service.emap.node;

import java.util.List;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.node.NodeType;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface NodeTypeService extends BaseService<NodeType> {

	public NodeType findByCode(String code);
	
	public NodeType findByName(String name);
}
