package com.pepper.service.emap.report;

import java.util.Date;
import java.util.Map;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseService;
import com.pepper.model.emap.report.Report;

public interface ReportService extends BaseService<Report> {

	public Pager<Map<String,Object>> findNodeTypeAndMap(Pager<Map<String,Object>> pager, String nodeTypeId, String mapId);
	
	public Integer findNodeCout(String nodeTypeId, String mapId);

	public Integer findNodeCout(String nodeTypeId, String mapId, Date startDate, Date endDate);
}
