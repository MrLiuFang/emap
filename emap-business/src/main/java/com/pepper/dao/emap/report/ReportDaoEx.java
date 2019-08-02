package com.pepper.dao.emap.report;

import java.util.Map;

import com.pepper.core.Pager;

public interface ReportDaoEx {
	
	public Pager<Map<String,Object>> findNodeTypeAndMap(Pager<Map<String,Object>> pager, String nodeTypeId, String mapId);
	
	public Integer findNodeCout(String nodeTypeId, String mapId);

}
