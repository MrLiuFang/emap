package com.pepper.service.emap.node.impl;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.node.NearbyNodeDao;
import com.pepper.model.emap.node.NearbyNode;
import com.pepper.service.emap.node.NearbyNodeService;

@Service(interfaceClass=NearbyNodeService.class )
public class NearbyNodeServiceImpl extends BaseServiceImpl<NearbyNode> implements NearbyNodeService {
	
	@Resource
	private NearbyNodeDao nearbyNodeDao;

	@Override
	public void deleteByNodeId(String nodeId) {
		nearbyNodeDao.deleteByNodeId(nodeId);
	}
}
