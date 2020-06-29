package com.pepper.service.emap.group;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseService;
import com.pepper.model.emap.group.Group;
import com.pepper.model.emap.group.GroupUser;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-24 10:47
 */
public interface GroupUserService extends BaseService<GroupUser> {

    @Transactional
    public int delete(String groupId,String userId);

    public Pager<GroupUser> query(Pager<GroupUser> pager, String groupId, String userId, Boolean isOperator, Boolean isLeader, String account, String name, String mobile);
}
