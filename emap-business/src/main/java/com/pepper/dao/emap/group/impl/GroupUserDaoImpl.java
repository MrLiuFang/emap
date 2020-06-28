package com.pepper.dao.emap.group.impl;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseDao;
import com.pepper.dao.emap.group.GroupUserDao;
import com.pepper.dao.emap.group.GroupUserDaoEx;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.group.GroupUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-27 20:00
 */
public class GroupUserDaoImpl implements GroupUserDaoEx {

    @Autowired
    private BaseDao<GroupUser> baseDao;

    public Pager<GroupUser> query(Pager<GroupUser> pager, String groupId, String userId, Boolean isOperator, Boolean isLeader, String account, String name, String mobile){
        StringBuilder jpql = new StringBuilder();
        Map<String,Object> searchParameter = new HashMap<String, Object>();
        jpql.append("select t1 from GroupUser t1 join AdminUser t2 on t1.userId = t2.id  where 1=1 ");
        if(StringUtils.hasText(groupId)){
            jpql.append(" and t1.groupId = :groupId ");
            searchParameter.put("groupId",groupId);
        }

        if(StringUtils.hasText(userId)){
            jpql.append(" and t1.userId = :userId ");
            searchParameter.put("userId",userId);
        }

        if(Objects.nonNull(isOperator)){
            jpql.append(" and t1.isOperator = :isOperator ");
            searchParameter.put("isOperator",isOperator);
        }
        if(Objects.nonNull(isLeader)){
            jpql.append(" and t1.isLeader = :isLeader ");
            searchParameter.put("isLeader",isLeader);
        }

        if(StringUtils.hasText(account)){
            jpql.append(" and t2.account like :account ");
            searchParameter.put("account","%"+account+"%");
        }

        if(StringUtils.hasText(name)){
            jpql.append(" and t2.name like :name ");
            searchParameter.put("name","%"+name+"%");
        }

        if(StringUtils.hasText(mobile)){
            jpql.append(" and t2.mobile like :mobile ");
            searchParameter.put("mobile","%"+mobile+"%");
        }

        return baseDao.findNavigator(pager, jpql.toString(), searchParameter);
    }
}
