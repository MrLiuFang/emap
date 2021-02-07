package com.pepper.dao.emap.node.impl;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseDao;
import com.pepper.dao.emap.node.NodeGroupDaoEx;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: 雄大
 * @create: 2021-02-07 14:28
 */
public class NodeGroupDaoImpl implements NodeGroupDaoEx {
    @Autowired
    private BaseDao<NodeGroup> baseDao;


    @Override
    public Pager<NodeGroup> findNavigator(Pager<NodeGroup> pager,String code,String name ) {
        StringBuffer jpql = new StringBuffer(" select new NodeGroup(ng.name,ng.code) from NodeGroup ng where 1=1   ");
        Map<String, Object> parameter = new HashMap<String,Object>();
        if (StringUtils.hasText(code)) {
            jpql.append(" and ng.code like :code ");
            parameter.put("code","%"+code+"%");
        }
        if (StringUtils.hasText(name)) {
            jpql.append(" and ng.name like :name ");
            parameter.put("name","%"+name+"%");
        }
        jpql.append(" group by ng.code,ng.name  ");

        pager = baseDao.findNavigator(pager, jpql.toString(), parameter);
        return pager;
    }
}
