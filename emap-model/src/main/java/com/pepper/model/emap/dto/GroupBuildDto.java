package com.pepper.model.emap.dto;

import com.pepper.model.emap.group.GroupBuild;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-29 13:43
 */
public class GroupBuildDto extends GroupBuild {

    List<String> buildIds;

    public List<String> getBuildIds() {
        return buildIds;
    }

    public void setBuildIds(List<String> buildIds) {
        this.buildIds = buildIds;
    }
}
