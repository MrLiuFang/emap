package com.pepper.service.emap.report;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.Pager;

@Service(interfaceClass = ReportService.class)
public class ReportServiceImpl implements ReportService {

	@Resource
	private com.pepper.dao.emap.report.ReportDao reportDao;

	@Override
	public Pager<Map<String,Object>> findNodeTypeAndMap(Pager<Map<String,Object>> pager, String nodeTypeId, String mapId) {
		return reportDao.findNodeTypeAndMap(pager,nodeTypeId,mapId);
	}

	@Override
	public Integer findNodeCout(String nodeTypeId, String mapId) {
		return reportDao.findNodeCout(nodeTypeId, mapId);
	}
}
