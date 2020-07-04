package com.pepper.service.emap.group;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseService;
import com.pepper.model.emap.group.GroupBuild;
import com.pepper.model.emap.log.SystemLog;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-24 10:47
 */
public interface GroupBuildService extends BaseService<GroupBuild> {

    public Pager<GroupBuild> query(Pager<GroupBuild> pager, String buildId, String groupId, String name, String code);

    @Transactional
    public int delete(String groupId,String buildId);

    public List<String> findBuildId(String userId);
}
