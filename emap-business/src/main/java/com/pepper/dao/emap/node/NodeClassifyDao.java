package com.pepper.dao.emap.node;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.node.NodeClassify;

public interface NodeClassifyDao  extends BaseDao<NodeClassify> {

    NodeClassify findFirstByCode(String code);

}
