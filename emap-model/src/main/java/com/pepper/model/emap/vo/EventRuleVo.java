package com.pepper.model.emap.vo;

import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.node.Node;

/**
 * 
 * @author Mr.Liu
 *
 */
public class EventRuleVo extends EventRule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1355323753898476887L;
	private Node node;
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	
	
}
