package com.pepper.model.emap.vo;

import java.util.List;

import com.pepper.model.emap.node.Node;

public class NearbyNodeVo {

	private NodeVo node;
	
	private List<NodeVo> nearbyNode;

	public NodeVo getNode() {
		return node;
	}

	public void setNode(NodeVo node) {
		this.node = node;
	}

	public List<NodeVo> getNearbyNode() {
		return nearbyNode;
	}

	public void setNearbyNode(List<NodeVo> nearbyNode) {
		this.nearbyNode = nearbyNode;
	}

	
	
	
	
}
