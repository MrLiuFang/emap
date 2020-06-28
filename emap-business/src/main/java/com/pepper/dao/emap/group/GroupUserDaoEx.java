package com.pepper.dao.emap.group;

import com.pepper.core.Pager;
import com.pepper.model.emap.group.GroupUser;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-27 20:00
 */
public interface GroupUserDaoEx {
    public Pager<GroupUser> query(Pager<GroupUser> pager, String groupId, String userId, Boolean isOperator, Boolean isLeader, String account, String name, String mobile);
}
