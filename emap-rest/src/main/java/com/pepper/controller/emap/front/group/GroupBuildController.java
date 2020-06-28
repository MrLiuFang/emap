package com.pepper.controller.emap.front.group;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.core.Pager;
import com.pepper.core.ResultData;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.building.BuildingInfo;
import com.pepper.model.emap.group.Group;
import com.pepper.model.emap.group.GroupBuild;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.vo.BuildingInfoVo;
import com.pepper.model.emap.vo.GroupBuildVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.building.BuildingInfoService;
import com.pepper.service.emap.group.GroupBuildService;
import com.pepper.service.emap.group.GroupService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.site.SiteInfoService;
import org.apache.dubbo.config.annotation.Reference;
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

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-24 11:19
 */
@Controller()
@RequestMapping(value = "/front/group/build")
public class GroupBuildController extends BaseControllerImpl implements BaseController {
    @Reference
    private GroupBuildService groupBuildService;

    @Reference
    private GroupService groupService;

    @Reference
    private BuildingInfoService buildingInfoService;

    @Reference
    private SystemLogService systemLogService;

    @Reference
    private SiteInfoService siteInfoService;

    @RequestMapping("/add")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object add(@RequestBody GroupBuild groupBuild){
        ResultData resultData = new ResultData();
        this.groupBuildService.save(groupBuild);
        systemLogService.log("groupBuild add", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/list")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object list(String buildId, String groupId, String name, String code) {
        Pager<GroupBuild> pager = new Pager<GroupBuild>();
        pager = groupBuildService.query(pager,buildId,groupId,name,code);
        pager.setData("groupBuild",convertVo(pager.getResults()));
        pager.setResults(null);
        return pager;
    }

    @RequestMapping(value = "/update")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object update(@RequestBody GroupBuild groupBuild ) throws IOException {
        ResultData resultData = new ResultData();
        groupBuildService.update(groupBuild);
        systemLogService.log("groupBuild update", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/toEdit")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object toEdit(String id) {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
        resultData.setData("groupBuild",convertVo(this.groupBuildService.findById(id)));
//        systemLogService.log("get groupBuild info", this.request.getRequestURL().toString());
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
                groupBuildService.deleteById(id);
            }catch (Exception e) {
                // TODO: handle exception
            }
        }
        systemLogService.log("groupBuild delete", this.request.getRequestURL().toString());
        return resultData;
    }

    private GroupBuildVo convertVo(GroupBuild groupBuild){
        GroupBuildVo groupBuildVo = new GroupBuildVo();
        BeanUtils.copyProperties(groupBuild,groupBuildVo);
        groupBuildVo.setGroup(groupService.findById(groupBuild.getGroupId()));
        BuildingInfo buildingInfo = this.buildingInfoService.findById(groupBuild.getBuildId());
        BuildingInfoVo buildingInfoVo = new BuildingInfoVo();
        BeanUtils.copyProperties(buildingInfo, buildingInfoVo);
        SiteInfo siteInfo = siteInfoService.findById(buildingInfo.getSiteInfoId());
        buildingInfoVo.setSite(siteInfo);
        groupBuildVo.setBuildingInfo(buildingInfoVo);
        return groupBuildVo;
    }

    private List<GroupBuildVo> convertVo(List<GroupBuild> list){
        List<GroupBuildVo> returnList = new ArrayList<GroupBuildVo>();
        for (GroupBuild groupBuild : list){
            returnList.add(convertVo(groupBuild));
        }
        return returnList;
    }
}
