package com.pepper.dao.emap.node;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.node.Node;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface NodeDao extends BaseDao<Node> , NodeDaoEx<Node> {

	/**
	 * 
	 * @param sourceCode
	 * @return
	 */
	public Node findFirstBySourceCode(String sourceCode);
	
	/**
	 * 根据地图ID查询有事件的设备
	 * @param mapId
	 * @return
	 */
	@Query("select t2 from EventList t1 join Node t2 on t1.sourceCode = t2.sourceCode join Map t3 on t2.mapId = t3.id"
			+ " where t1.status <> 'P' AND t2.x <> '' AND t2.x IS NOT NULL AND t2.y <> '' AND t2.y IS NOT NULL and t3.id = ?1  ")
	public List<Node> findByMapIdAndHasEvent(String mapId);
	
	/**
	 * 根据code查询设备
	 * @param code
	 * @return
	 */
	public Node findFirstByCode(String code);
}
