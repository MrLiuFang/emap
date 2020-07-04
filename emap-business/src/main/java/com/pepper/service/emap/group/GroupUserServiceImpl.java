package com.pepper.service.emap.group;

import com.pepper.core.Pager;
import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.group.GroupUserDao;
import com.pepper.model.emap.group.GroupUser;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-24 10:57
 */
@Service(interfaceClass = GroupUserService.class)
public class GroupUserServiceImpl extends BaseServiceImpl<GroupUser> implements GroupUserService {

    @Autowired
    private GroupUserDao groupUserDao;

    @Override
    public int delete(String groupId,String userId) {
        return groupUserDao.deleteByGroupIdAndUserId(groupId,userId);
    }

    @Override
    public Pager<GroupUser> query(Pager<GroupUser> pager, String groupId, String userId, Boolean isOperator, Boolean isLeader, String account, String name, String mobile) {
        return groupUserDao.query(pager,groupId,userId,isOperator,isLeader,account,name,mobile);
    }

    @Override
    public List<GroupUser> findGroupUser(String userId) {
        return groupUserDao.findByUserId(userId);
    }
}
