package com.pepper.dao.emap.node;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.node.NodeGroup;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: 雄大
 * @create: 2021-02-04 15:48
 */
public interface NodeGroupDao extends BaseDao<NodeGroup> ,NodeGroupDaoEx {

    public void deleteAllByCode(String code);

    public List<NodeGroup> findAllByCode(String code);

    @Query("select distinct t1 from NodeGroup t1 where t1.code in (select distinct t2.code from NodeGroup t2 where t2.nodeId = :nodeId and t2.isMaster is true )")
    public List<NodeGroup> findAllByNodeId(String nodeId);

    @Query(" select code from NodeGroup where nodeId =:nodeId group by code")
    public List<String> findNodeGroupCodeByNodeId(String nodeId);

    @Query( " select distinct nf.outPort  from EventListGroup eg join Node nf on eg.nodeId = nf.id\n" +
            " where nf.outIp = :outIp and nf.port = :port and eg.status <> 'P' and nf.outPort>0 " )
    public List<Integer> findAllOutPortOn(String outIp, Integer port);


    public NodeGroup findFirstByNodeIdAndIsMaster(String nodeId,Boolean isMaster);

}
