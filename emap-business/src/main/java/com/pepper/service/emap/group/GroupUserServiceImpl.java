package com.pepper.service.emap.group;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.group.GroupUserDao;
import com.pepper.model.emap.group.GroupUser;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-24 10:57
 */
@Service(interfaceClass = GroupUserService.class)
public class GroupUserServiceImpl extends BaseServiceImpl<GroupUser> implements GroupUserService {

    @Autowired
    private GroupUserDao groupUserDao;
}
