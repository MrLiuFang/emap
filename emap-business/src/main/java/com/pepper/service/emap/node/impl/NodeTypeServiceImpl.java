package com.pepper.service.emap.node.impl;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.model.emap.node.NodeType;
import com.pepper.service.emap.node.NodeTypeService;
/**
 * 
 * @author Mr.Liu
 *
 */
@Service(interfaceClass=NodeTypeService.class)
public class NodeTypeServiceImpl extends BaseServiceImpl<NodeType> implements NodeTypeService {

}
