package com.pepper.model.emap.node;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_node_info")
@DynamicUpdate(true)
public class Node extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2480337669147928582L;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "source")
	private String source;
	
	@Column(name = "source_code")
	private String sourceCode;
	
	@Column(name = "map_id")
	private String mapId;
	
	@Column(name = "node_type_id")
	private String nodeTypeId;
	
	@Column(name = "parent_node")
	private String parentNode;
	
	@Column(name = "external_link")
	private String externalLink;
	
	@Column(name = "warning_level")
	private Integer warningLevel;
	
	@Column(name = "x")
	private String x;
	
	@Column(name = "y")
	private String y;

	@Column(name = "department_id")
	private String departmentId;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getNodeTypeId() {
		return nodeTypeId;
	}

	public void setNodeTypeId(String nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
	}

	public String getParentNode() {
		return parentNode;
	}

	public void setParentNode(String parentNode) {
		this.parentNode = parentNode;
	}

	public String getExternalLink() {
		return externalLink;
	}

	public void setExternalLink(String externalLink) {
		this.externalLink = externalLink;
	}

	public Integer getWarningLevel() {
		return warningLevel;
	}

	public void setWarningLevel(Integer warningLevel) {
		this.warningLevel = warningLevel;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	
	
	
}
