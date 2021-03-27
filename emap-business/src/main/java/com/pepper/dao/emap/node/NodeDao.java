package com.pepper.dao.emap.node;

import java.util.List;

import org.hibernate.annotations.Where;
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
	@Query(nativeQuery=true, value="select distinct t2.* from t_event_list t1 join t_node_info t2 on t1.source_code = t2.source_code join t_map_info t3 on t2.map_id = t3.id"
			+ " where t1.status <> 'P' and t3.id = ?1  ")
	public List<Node> findByMapIdAndHasEvent(String mapId);
	
	/**
	 * 根据code查询设备
	 * @param code
	 * @return
	 */
	public Node findFirstByCode(String code);
	
	public Node findFirstByName(String name);
	
	public List<Node> findByNameLike(String name);

	public Node findFirstByOutIpAndPortAndIdNot(String ip, Integer port, String id);

	@Query(" select n from Node n join NodeGroup ng on n.id = ng.nodeId where ng.code = :code and ng.isMaster = true ")
	public Node findNodeGroupMaster(String code);
}
