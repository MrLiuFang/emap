package com.pepper.dao.emap.node;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.node.NearbyNode;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface NearbyNodeDao  extends BaseDao<NearbyNode>{

	@Query(value="delete from NearbyNode  where nodeId  = ?1  ")
	@Modifying
	public void deleteByNodeId(String nodeId);
}
