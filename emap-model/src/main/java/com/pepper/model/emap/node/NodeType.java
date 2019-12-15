package com.pepper.model.emap.node;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;
import com.pepper.model.emap.enums.Classify;

@Entity()
@Table(name = "t_node_type_info")
@DynamicUpdate(true)
public class NodeType extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7192315061647641454L;

	@Column(name = "code",unique = true,nullable=false)
	private String code;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "working_icon")
	private String workingIcon;
	
	@Column(name = "stop_icon")
	private String stopIcon;
	
	@Column(name = "processing_icon")
	private String processingIcon;
	
	@Column(name = "is_lock")
	private Boolean isLock;
	
	@Column(name = "classify")
	private Classify classify;
	
	public Boolean getIsLock() {
		return isLock;
	}

	public void setIsLock(Boolean isLock) {
		this.isLock = isLock;
	}

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

	public String getWorkingIcon() {
		return workingIcon;
	}

	public void setWorkingIcon(String workingIcon) {
		this.workingIcon = workingIcon;
	}

	public String getStopIcon() {
		return stopIcon;
	}

	public void setStopIcon(String stopIcon) {
		this.stopIcon = stopIcon;
	}

	public String getProcessingIcon() {
		return processingIcon;
	}

	public void setProcessingIcon(String processingIcon) {
		this.processingIcon = processingIcon;
	}

	public Classify getClassify() {
		return classify;
	}

	public void setClassify(Classify classify) {
		this.classify = classify;
	}
	
	
}
