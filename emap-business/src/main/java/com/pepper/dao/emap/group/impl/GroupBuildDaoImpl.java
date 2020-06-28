package com.pepper.dao.emap.group.impl;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseDao;
import com.pepper.dao.emap.group.GroupBuildDao;
import com.pepper.dao.emap.group.GroupBuildDaoEx;
import com.pepper.model.emap.group.GroupBuild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Reference;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-27 20:43
 */
public class GroupBuildDaoImpl implements GroupBuildDaoEx {

    @Autowired
    private BaseDao<GroupBuild> baseDao;

    public Pager<GroupBuild> query(Pager<GroupBuild> pager, String buildId, String groupId, String name, String code){
        StringBuilder jpql = new StringBuilder();
        Map<String,Object> searchParameter = new HashMap<String, Object>();
        jpql.append("select t1 from GroupBuild t1 join BuildingInfo t2 on t1.buildId = t2.id where 1=1 ");
        if(StringUtils.hasText(buildId)){
            jpql.append(" and t1.buildId=:buildId ");
            searchParameter.put("buildId",buildId);
        }
        if(StringUtils.hasText(groupId)){
            jpql.append(" and t1.groupId = :groupId ");
            searchParameter.put("groupId",groupId);
        }
        if(StringUtils.hasText(name)){
            jpql.append(" and t2.name like :name ");
            searchParameter.put("name",name);
        }
        if(StringUtils.hasText(code)){
            jpql.append(" and t2.code like :code ");
            searchParameter.put("code",code);
        }
        return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
    }
}
