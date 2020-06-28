package com.pepper.dao.emap.group;

import com.pepper.core.Pager;
import com.pepper.model.emap.group.GroupBuild;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-27 20:43
 */
public interface GroupBuildDaoEx {
    public Pager<GroupBuild> query(Pager<GroupBuild> pager, String buildId, String groupId, String name, String code);
}
