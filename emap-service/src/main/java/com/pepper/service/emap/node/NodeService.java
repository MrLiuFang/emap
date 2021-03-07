package com.pepper.service.emap.node;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;

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
	public Pager<Node> findNavigator(Pager<Node> pager,String code,String name,String source,String sourceCode,String mapId,String nodeTypeId,String siteId,String buildId,String floor,String hasXY,String keyWord);
	
	/**
	 * 
	 * @param sourceCode
	 * @return
	 */
	public Node findBySourceCode(String sourceCode);
	
	/**
	 * 根据地图ID查询有事件的设备
	 * @param mapId
	 * @return
	 */
	public List<Node> findByMapIdAndHasEvent(String mapId);
	
	/**
	 * 根据code查询设备
	 * @param code
	 * @return
	 */
	public Node findByCode(String code);
	
	public Node findByName(String name);

	List<Node> findByNameLike(String name);

	public Node findFirstByOutIpAndPortAndIdNot(String ip, Integer port, String id);

	Node findFirstBySourceCode(String sourceCode);
}
