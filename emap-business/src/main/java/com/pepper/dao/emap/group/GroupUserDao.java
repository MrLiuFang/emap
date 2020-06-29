package com.pepper.dao.emap.group;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.group.GroupUser;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-24 11:03
 */
public interface GroupUserDao extends BaseDao<GroupUser> ,GroupUserDaoEx {

    public int deleteByGroupId(String groupId);

    public int deleteByUserId(String userId);

    public int deleteByGroupIdAndUserId(String groupId,String userId);
}
