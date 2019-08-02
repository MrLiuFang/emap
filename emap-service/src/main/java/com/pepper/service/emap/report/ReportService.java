package com.pepper.service.emap.report;

import java.util.List;
import java.util.Map;

import com.pepper.core.Pager;

public interface ReportService {

	public Pager<Map<String,Object>> findNodeTypeAndMap(Pager<Map<String,Object>> pager, String nodeTypeId, String mapId);
	
	public Integer findNodeCout(String nodeTypeId, String mapId);
}
