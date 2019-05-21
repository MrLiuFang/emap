package com.pepper.service.emap.node;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.node.NodeType;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface NodeTypeService extends BaseService<NodeType> {

	public NodeType findByCode(String code);
}
