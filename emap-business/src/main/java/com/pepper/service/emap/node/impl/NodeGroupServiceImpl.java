package com.pepper.service.emap.node.impl;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.node.NodeGroupDao;
import com.pepper.model.emap.node.NodeGroup;
import com.pepper.service.emap.node.NodeGroupService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @author: 雄大
 * @create: 2021-02-04 15:57
 */
@Service(interfaceClass = NodeGroupService.class )
public class NodeGroupServiceImpl extends BaseServiceImpl<NodeGroup> implements NodeGroupService {

    @Autowired
    private NodeGroupDao nodeGroupDao;

    @Override
    @Transactional
    public void deleteAllByCode(String code) {
        nodeGroupDao.deleteAllByCode(code);
    }

    @Override
    public List<NodeGroup> find(String code) {
        return nodeGroupDao.findAllByCode(code);
    }

    @Override
    public List<NodeGroup> findAllByNodeId(String nodeId) {
        return nodeGroupDao.findAllByNodeId(nodeId);
    }
}