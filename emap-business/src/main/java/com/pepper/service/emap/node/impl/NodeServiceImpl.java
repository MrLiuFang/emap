package com.pepper.service.emap.node.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.Pager;
import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.node.NodeDao;
import com.pepper.model.emap.node.Node;
import com.pepper.service.emap.node.NodeService;

@Service(interfaceClass=NodeService.class)
public class NodeServiceImpl extends BaseServiceImpl<Node> implements NodeService {
	
	@Resource
	private NodeDao nodeDao;

	@Override
	public Pager<Node> findNavigator(Pager<Node> pager, Map<String, Object> parameter) {
		return nodeDao.findNavigator(pager, parameter);
	}

	

}
