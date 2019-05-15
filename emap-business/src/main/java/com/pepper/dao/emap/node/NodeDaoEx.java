package com.pepper.dao.emap.node;

import java.util.Map;

import com.pepper.core.Pager;
import com.pepper.model.emap.node.Node;

public interface NodeDaoEx<T> {

	Pager<Node> findNavigator(Pager<Node> pager,Map<String, Object> parameter);
	
	public Pager<Node> findNavigator(Pager<Node> pager,String code,String name,String source,String sourceCode,String mapId,String nodeTypeId,String siteId,String buildId,String floor) ;
}
