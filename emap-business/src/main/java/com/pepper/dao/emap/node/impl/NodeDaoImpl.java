package com.pepper.dao.emap.node.impl;

import java.util.HashMap;
import java.util.Map;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseDao;
import com.pepper.core.base.curd.DaoExImpl;
import com.pepper.dao.emap.node.NodeDaoEx;
import com.pepper.model.emap.node.Node;

/**
 * 
 * @author Mr.Liu
 *
 */
public class NodeDaoImpl extends DaoExImpl<Node> implements NodeDaoEx<Node> {

	@Override
	public Pager<Node> findNavigator(Pager<Node> pager,Map<String, Object> parameter) {
		BaseDao<Node>  baseDao = this.getPepperSimpleJpaRepository(this.getClass());
		StringBuffer jpql = new StringBuffer("from Node where id =:id");
		parameter = new HashMap<String, Object>();
		parameter.put("id", "value");
		pager = baseDao.findNavigator(pager, jpql.toString(), parameter);
		return pager;
	}

	
}
