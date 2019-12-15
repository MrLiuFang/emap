package com.pepper.dao.emap.report;

import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.report.ReportParameter;

public interface ReportParameterDao extends BaseDao<ReportParameter> {

	List<ReportParameter> findByReportId(String reportId);
}
