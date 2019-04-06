package com.pepper.model.emap.node;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_nearby_node")
@DynamicUpdate(true)
public class NearbyNode  extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6897127330587140083L;

	@Column(name = "node_id")
	private String nodeId;
	
	@Column(name = "nearby_node_id")
	private String nearbyNodeId;

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNearbyNodeId() {
		return nearbyNodeId;
	}

	public void setNearbyNodeId(String nearbyNodeId) {
		this.nearbyNodeId = nearbyNodeId;
	}
	
}
