package com.pepper.service.emap.node;

import org.springframework.transaction.annotation.Transactional;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.node.NearbyNode;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface NearbyNodeService  extends BaseService<NearbyNode> {

	/**
	 * 根据设备ID删除附近的设备
	 * @param nodeId
	 */
	@Transactional
	public void deleteByNodeId(String nodeId);
}
