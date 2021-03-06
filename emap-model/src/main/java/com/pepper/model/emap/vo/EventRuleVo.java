package com.pepper.model.emap.vo;

import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;

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
	
	private Department department;
	
	private Department specialDepartment;
	
	private NodeType nodeType;
	
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public NodeType getNodeType() {
		return nodeType;
	}
	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	public Department getSpecialDepartment() {
		return specialDepartment;
	}
	public void setSpecialDepartment(Department specialDepartment) {
		this.specialDepartment = specialDepartment;
	}
	
	

}
