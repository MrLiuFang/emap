package com.pepper.model.emap.vo;

import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.node.Node;

/**
 * 
 * @author Mr.Liu
 *
 */
public class EventListVo extends EventList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5936683481323862183L;
	
	private Node node;

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
}
