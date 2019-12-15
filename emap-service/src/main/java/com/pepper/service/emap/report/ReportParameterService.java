package com.pepper.service.emap.report;

import java.util.List;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.report.ReportParameter;

public interface ReportParameterService extends BaseService<ReportParameter> {

	List<ReportParameter> findReportParameter(String reportId);
}
