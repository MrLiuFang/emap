package com.pepper.model.emap.vo;

import com.pepper.model.emap.node.Node;

public class NodeVo extends Node {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2816383793529586101L;

	private MapVo map;
	
	private NodeTypeVo nodeType;

	public MapVo getMap() {
		return map;
	}

	public void setMap(MapVo map) {
		this.map = map;
	}

	public NodeTypeVo getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeTypeVo nodeType) {
		this.nodeType = nodeType;
	}
	
	
}
