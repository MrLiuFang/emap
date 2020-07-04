package com.pepper.service.emap.group;

import com.pepper.core.Pager;
import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.group.GroupBuildDao;
import com.pepper.model.emap.group.GroupBuild;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-24 11:01
 */
@Service(interfaceClass = GroupBuildService.class)
public class GroupBuildServiceImpl extends BaseServiceImpl<GroupBuild> implements GroupBuildService{

    @Autowired
    private GroupBuildDao groupBuildDao;

    @Override
    public Pager<GroupBuild> query(Pager<GroupBuild> pager, String buildId, String groupId, String name, String code) {
        return groupBuildDao.query(pager,buildId,groupId,name,code);
    }

    @Override
    public int delete(String groupId, String buildId) {
        return groupBuildDao.deleteByGroupIdAndBuildId(groupId,buildId);
    }

    @Override
    public List<String> findBuildId(String userId) {
        List<String> list = groupBuildDao.findBuildIdByUserId(userId);
        if(Objects.isNull(list) || list.size()<=0){
            list = new ArrayList<String>();
            list.add(UUID.randomUUID().toString());
        }
        return list;
    }
}
