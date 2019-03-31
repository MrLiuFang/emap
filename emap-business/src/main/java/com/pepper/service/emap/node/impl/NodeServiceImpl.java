package com.pepper.service.emap.node.impl;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.model.emap.node.Node;
import com.pepper.service.emap.node.NodeService;

@Service(interfaceClass=NodeService.class)
public class NodeServiceImpl extends BaseServiceImpl<Node> implements NodeService {

	

}
