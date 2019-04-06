package com.pepper.model.emap.vo;

import java.util.List;

import com.pepper.model.emap.node.Node;

public class NearbyNodeVo {

	private Node node;
	
	private List<Node> nearbyNode;

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public List<Node> getNearbyNode() {
		return nearbyNode;
	}

	public void setNearbyNode(List<Node> nearbyNode) {
		this.nearbyNode = nearbyNode;
	}
	
	
	
}
