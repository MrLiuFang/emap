package com.pepper.model.emap.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_report_parameter")
@DynamicUpdate(true)
public class ReportParameter extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7299937584448848733L;
	
	@Column(name = "report_id")
	private String reportId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "type")
	private Integer type;
	
	@Column(name = "parameter")
	private String parameter;
	
	@Column(name = "default_value")
	private String defaultValue;
	
	@Column(name = "required")
	private Boolean required;

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}
	
	

}
