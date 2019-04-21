package com.pepper.service.emap.node;

import java.util.Map;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseService;
import com.pepper.model.emap.node.Node;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface NodeService extends BaseService<Node> {
	/**
	 * 
	 * @param pager
	 * @param parameter
	 * @return
	 */
	public Pager<Node> findNavigator(Pager<Node> pager,String code,String name,String source,String sourceCode,String mapId,String nodeTypeId,String siteId,String buildId,String floor);
	
	/**
	 * 
	 * @param sourceCode
	 * @return
	 */
	public Node findBySourceCode(String sourceCode);
}
