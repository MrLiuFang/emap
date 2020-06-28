package com.pepper.model.emap.dto;

import com.pepper.model.emap.group.GroupUser;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-27 09:27
 */
public class GroupUserDto extends GroupUser {
    private List<Map<String,Object>> users;

    public List<Map<String, Object>> getUsers() {
        return users;
    }

    public void setUsers(List<Map<String, Object>> users) {
        this.users = users;
    }
}
