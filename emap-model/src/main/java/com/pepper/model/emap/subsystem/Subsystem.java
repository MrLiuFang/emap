package com.pepper.model.emap.subsystem;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_subsystem")
@DynamicUpdate(true)
public class Subsystem extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1624190962577307936L;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "node_code")
	private String nodeCode;
	
	@Column(name = "prot")
	private Integer prot;
	
	@Column(name = "is_online")
	private Boolean isOnLine;
	
	@Column(name = "is_relation")
	private Boolean isRelation;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getProt() {
		return prot;
	}

	public void setProt(Integer prot) {
		this.prot = prot;
	}

	public Boolean getIsOnLine() {
		return isOnLine;
	}

	public void setIsOnLine(Boolean isOnLine) {
		this.isOnLine = isOnLine;
	}

	public Boolean getIsRelation() {
		return isRelation;
	}

	public void setIsRelation(Boolean isRelation) {
		this.isRelation = isRelation;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}
	
}
