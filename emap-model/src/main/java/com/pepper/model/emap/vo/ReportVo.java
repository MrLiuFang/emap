package com.pepper.model.emap.vo;

import java.util.List;

import com.pepper.model.emap.report.Report;
import com.pepper.model.emap.report.ReportParameter;

public class ReportVo extends Report  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5854702161321792299L;
	
	private List<ReportParameter> reportParameters;

	public List<ReportParameter> getReportParameters() {
		return reportParameters;
	}

	public void setReportParameters(List<ReportParameter> reportParameters) {
		this.reportParameters = reportParameters;
	}
	
	
}
