package com.pepper.model.emap.event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_help_list")
@DynamicUpdate(true)
public class HelpList extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1213445875503778464L;

	@Column(name = "code",unique = true)
	private String code;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "node_type_id",unique = true)
	private String nodeTypeId;
	
	@Column(name = "warning_level")
	private Integer warningLevel;
	
	@Column(name = "help_message")
	private String helpMessage;

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

	public String getNodeTypeId() {
		return nodeTypeId;
	}

	public void setNodeTypeId(String nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
	}

	public Integer getWarningLevel() {
		return warningLevel;
	}

	public void setWarningLevel(Integer warningLevel) {
		this.warningLevel = warningLevel;
	}

	public String getHelpMessage() {
		return helpMessage;
	}

	public void setHelpMessage(String helpMessage) {
		this.helpMessage = helpMessage;
	}

	
	
}
