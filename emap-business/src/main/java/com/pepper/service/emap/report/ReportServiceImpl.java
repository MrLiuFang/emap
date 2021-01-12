package com.pepper.service.emap.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.Pager;
import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.report.ReportParameterDao;
import com.pepper.model.emap.report.Report;

@Service(interfaceClass = ReportService.class)
public class ReportServiceImpl extends BaseServiceImpl<Report> implements ReportService {

	@Resource
	private com.pepper.dao.emap.report.ReportDao reportDao;
	
	@Resource
	private ReportParameterDao reportParameterDao;

	@Override
	public Pager<Map<String,Object>> findNodeTypeAndMap(Pager<Map<String,Object>> pager, String nodeTypeId, String mapId) {
		return reportDao.findNodeTypeAndMap(pager,nodeTypeId,mapId);
	}

	@Override
	public Integer findNodeCout(String nodeTypeId, String mapId) {
		return reportDao.findNodeCout(nodeTypeId, mapId);
	}

	@Override
	public Integer findNodeCout(String nodeTypeId, String mapId, Date startDate, Date endDate) {
		return reportDao.findNodeCout(nodeTypeId, mapId, startDate, endDate);
	}
}
