package com.pepper.service.emap.report;

import java.util.List;

import javax.annotation.Resource;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.report.ReportParameterDao;
import com.pepper.model.emap.report.ReportParameter;

@org.apache.dubbo.config.annotation.Service(interfaceClass = ReportParameterService.class)
public class ReportParameterServiceImpl extends BaseServiceImpl<ReportParameter> implements ReportParameterService {

	@Resource
	private ReportParameterDao reportParameterDao;

	@Override
	public List<ReportParameter> findReportParameter(String reportId) {
		return reportParameterDao.findByReportId(reportId);
	}
}
