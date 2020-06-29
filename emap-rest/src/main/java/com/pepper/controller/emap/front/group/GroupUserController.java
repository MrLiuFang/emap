package com.pepper.controller.emap.front.group;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.core.Pager;
import com.pepper.core.ResultData;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.dto.GroupUserDto;
import com.pepper.model.emap.group.GroupBuild;
import com.pepper.model.emap.group.GroupUser;
import com.pepper.model.emap.vo.AdminUserEventAssistVo;
import com.pepper.model.emap.vo.GroupBuildVo;
import com.pepper.model.emap.vo.GroupUserVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.emap.building.BuildingInfoService;
import com.pepper.service.emap.group.GroupBuildService;
import com.pepper.service.emap.group.GroupService;
import com.pepper.service.emap.group.GroupUserService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.file.FileService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.zookeeper.server.admin.AdminServer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-24 11:18
 */
@Controller()
@RequestMapping(value = "/front/group/user")
public class GroupUserController extends BaseControllerImpl implements BaseController {
    @Reference
    private GroupUserService groupUserService;

    @Reference
    private GroupService groupService;

    @Reference
    private AdminUserService adminUserService;

    @Reference
    private SystemLogService systemLogService;

    @Reference
    private FileService fileService;

    @RequestMapping("/add")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object add(@RequestBody GroupUserDto groupUserDto){
        ResultData resultData = new ResultData();

        List<Map<String,Object>> userIds = groupUserDto.getUsers();
        userIds.forEach(map ->{
            GroupUser groupUser= new GroupUser();
            BeanUtils.copyProperties(groupUserDto,groupUser);
            String userId= String.valueOf(map.get("userId"));
//            groupUserService.delete(userId);
            groupUser.setUserId(userId);
//            Boolean isOperator = Boolean.valueOf(map.get("isOperator").toString());
//            Boolean isLeader = Boolean.valueOf(map.get("isLeader").toString());
//            groupUser.setOperator(isOperator);
//            groupUser.setLeader(isLeader);
            groupUserService.delete(groupUser.getGroupId(),userId);
            this.groupUserService.save(groupUser);
        });
        systemLogService.log("groupUser add", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/list")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object list(String groupId, String userId,Boolean isOperator,Boolean isLeader,String account,String name,String mobile) {
        Pager<GroupUser> pager = new Pager<GroupUser>();
        pager = groupUserService.query(pager,groupId,userId,isOperator,isLeader,account,name,mobile);
        pager.setData("groupUser",convertVo(pager.getResults()));
        pager.setResults(null);
        return pager;
    }

    @RequestMapping(value = "/update")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object update(@RequestBody GroupUser groupUser ) throws IOException {
        ResultData resultData = new ResultData();
//        groupUserService.delete(groupUser.getUserId());
        groupUserService.update(groupUser);
        systemLogService.log("groupUser update", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/toEdit")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object toEdit(String id) {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
        resultData.setData("groupUser",convertVo(this.groupUserService.findById(id)));
        return resultData;
    }

    @RequestMapping(value = "/delete")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object delete(@RequestBody String str) throws IOException {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
        if(!StringUtils.hasText(str)){
            return resultData;
        }
        JsonNode jsonNode = new ObjectMapper().readTree(str);
        if(!jsonNode.has("id")) {
            return resultData;
        }
        ArrayNode arrayNode = (ArrayNode)jsonNode.get("id");
        for(int i = 0; i < arrayNode.size(); i++) {
            String id = arrayNode.get(i).asText();
            try {
                groupUserService.deleteById(id);
            }catch (Exception e) {
                // TODO: handle exception
            }
        }
        systemLogService.log("groupUser delete", this.request.getRequestURL().toString());
        return resultData;
    }

    private GroupUserVo convertVo(GroupUser groupUser){
        GroupUserVo groupUserVo = new GroupUserVo();
        BeanUtils.copyProperties(groupUser,groupUserVo);
        groupUserVo.setGroup(groupService.findById(groupUser.getGroupId()));
        groupUserVo.setAdminUser(convterVo(adminUserService.findById(groupUser.getUserId())));
        return groupUserVo;
    }

    private List<GroupUserVo> convertVo(List<GroupUser> list){
        List<GroupUserVo> returnList = new ArrayList<GroupUserVo>();
        for (GroupUser groupUser : list){
            if(Objects.isNull(groupUser)){
                continue;
            }
            returnList.add(convertVo(groupUser));
        }
        return returnList;
    }

    private AdminUserEventAssistVo convterVo(AdminUser adminUser){
        AdminUserEventAssistVo adminUserEventAssistVo = new AdminUserEventAssistVo();
        BeanUtils.copyProperties(adminUser,adminUserEventAssistVo);
        adminUserEventAssistVo.setHeadPortraitUrl(fileService.getUrl(adminUser.getHeadPortrait()));
        return adminUserEventAssistVo;
    }
}
