package com.pepper.service.emap.node.impl;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.node.NodeClassifyDao;
import com.pepper.model.emap.node.NodeClassify;
import com.pepper.service.emap.node.NodeClassifyService;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;

@Service(interfaceClass=NodeClassifyService.class)
public class NodeClassifyServiceImpl extends BaseServiceImpl<NodeClassify> implements NodeClassifyService {

    @Resource
    private NodeClassifyDao nodeClassifyDao;

    @Override
    public NodeClassify findNodeClassify(String code) {
        return nodeClassifyDao.findFirstByCode(code);
    }
}
