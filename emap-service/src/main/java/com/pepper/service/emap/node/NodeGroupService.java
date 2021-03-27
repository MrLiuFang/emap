package com.pepper.service.emap.node;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseService;
import com.pepper.model.emap.node.NodeClassify;
import com.pepper.model.emap.node.NodeGroup;

import java.util.List;

/**
 * @description:
 * @author: 雄大
 * @create: 2021-02-04 15:49
 */
public interface NodeGroupService extends BaseService<NodeGroup> {

    public void deleteAllByCode(String code);

    public List<NodeGroup> find(String code);

    public List<NodeGroup> findAllByNodeId(String nodeId);

    public List<String> findNodeGroupCodeByNodeId(String nodeId);

    public Pager<NodeGroup> findNavigator(Pager<NodeGroup> pager, String code, String name );

    public List<Integer> findAllOutPortOn(String outIp, Integer port);

    public NodeGroup find(String nodeId,Boolean isMaster);
}
