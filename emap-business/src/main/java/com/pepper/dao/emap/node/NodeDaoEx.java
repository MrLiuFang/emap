package com.pepper.dao.emap.node;

import java.util.Map;

import com.pepper.core.Pager;
import com.pepper.model.emap.node.Node;

public interface NodeDaoEx<T> {

	Pager<Node> findNavigator(Pager<Node> pager,Map<String, Object> parameter);
}
