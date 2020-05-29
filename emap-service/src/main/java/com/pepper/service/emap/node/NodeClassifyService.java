package com.pepper.service.emap.node;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.node.NodeClassify;

public interface NodeClassifyService  extends BaseService<NodeClassify> {

    NodeClassify findNodeClassify(String code);

}
