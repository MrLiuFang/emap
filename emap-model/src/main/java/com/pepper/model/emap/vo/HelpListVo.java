package com.pepper.model.emap.vo;

import com.pepper.model.emap.event.HelpList;
import com.pepper.model.emap.node.NodeType;

public class HelpListVo extends HelpList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3662225562072782257L;
	
	
	private NodeType nodeType;


	public NodeType getNodeType() {
		return nodeType;
	}


	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	
	

}
