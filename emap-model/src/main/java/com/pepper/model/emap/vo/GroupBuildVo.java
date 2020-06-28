package com.pepper.model.emap.vo;

import com.pepper.model.emap.building.BuildingInfo;
import com.pepper.model.emap.group.Group;
import com.pepper.model.emap.group.GroupBuild;
import com.pepper.model.emap.group.GroupUser;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-24 14:36
 */
public class GroupBuildVo extends GroupBuild {

    private BuildingInfoVo buildingInfo;

    private Group group;

    public BuildingInfoVo getBuildingInfo() {
        return buildingInfo;
    }

    public void setBuildingInfo(BuildingInfoVo buildingInfo) {
        this.buildingInfo = buildingInfo;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
