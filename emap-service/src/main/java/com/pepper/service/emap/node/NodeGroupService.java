package com.pepper.service.emap.node;

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
}
