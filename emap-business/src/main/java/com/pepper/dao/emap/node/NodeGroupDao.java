package com.pepper.dao.emap.node;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.node.NodeGroup;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @description:
 * @author: 雄大
 * @create: 2021-02-04 15:48
 */
public interface NodeGroupDao extends BaseDao<NodeGroup> {

    public void deleteAllByCode(String code);

    public List<NodeGroup> findAllByCode(String code);

    @Query("select t1 from NodeGroup t1 where t1.code in (select t2.code from NodeGroup t2 where t2.nodeId = :nodeId ) and t1.nodeId <> :nodeId")
    public List<NodeGroup> findAllByNodeId(String nodeId);
}
