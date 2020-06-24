package com.pepper.service.emap.group;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.group.GroupDao;
import com.pepper.model.emap.group.Group;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-24 10:59
 */
@Service(interfaceClass = GroupService.class)
public class GroupServiceImpl extends BaseServiceImpl<Group> implements GroupService {

    @Autowired
    private GroupDao groupDao;
}
