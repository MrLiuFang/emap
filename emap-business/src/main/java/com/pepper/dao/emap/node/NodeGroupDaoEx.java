package com.pepper.dao.emap.node;

import com.pepper.core.Pager;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeGroup;

import java.util.Map;

/**
 * @description:
 * @author: 雄大
 * @create: 2021-02-07 14:27
 */
public interface NodeGroupDaoEx {


    public Pager<NodeGroup> findNavigator(Pager<NodeGroup> pager,String code,String name );
}
