package com.pepper.model.emap.vo;

import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.group.Group;
import com.pepper.model.emap.group.GroupBuild;
import com.pepper.model.emap.group.GroupUser;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-24 14:38
 */
public class GroupUserVo extends GroupUser  {

    private AdminUser adminUser;

    private Group group;

    public AdminUser getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(AdminUser adminUser) {
        this.adminUser = adminUser;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
