package com.pepper.dao.emap.group;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.group.GroupBuild;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-24 11:04
 */
public interface GroupBuildDao extends BaseDao<GroupBuild> ,GroupBuildDaoEx {

    public int deleteByGroupIdAndBuildId(String groupId,String buildId);

    @Query(" select Distinct t1.buildId from GroupBuild t1 join GroupUser t2 on t1.groupId = t2.groupId where t2.userId= :userId")
    public List<String> findBuildIdByUserId(@Param("userId") String userId);
}
