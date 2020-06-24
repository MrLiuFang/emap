package com.pepper.controller.emap.front.group;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.ResultData;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.group.Group;
import com.pepper.model.emap.map.Map;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.group.GroupService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.util.MapToBeanUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2020-06-24 11:13
 */
@Controller()
@RequestMapping(value = "/front/group")
public class GroupController extends BaseControllerImpl implements BaseController {

    @Reference
    private GroupService groupService;

    @Reference
    private SystemLogService systemLogService;

    @RequestMapping("/add")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object add(@RequestBody Group group){
        ResultData resultData = new ResultData();
        this.groupService.save(group);
        systemLogService.log("group add", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/list")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object list(String code,String name) {
        Pager<Group> pager = new Pager<Group>();

        if(StringUtils.hasText(code)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_code",code );
        }
        if(StringUtils.hasText(name)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
        }
        pager = groupService.findNavigator(pager);
        pager.setData("group",pager.getResults());
        pager.setResults(null);
        return pager;
    }

    @RequestMapping(value = "/update")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object update(@RequestBody Group group ) throws IOException {
        ResultData resultData = new ResultData();
        groupService.update(group);
        systemLogService.log("group update", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/toEdit")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object toEdit(String id) {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
        resultData.setData("group",this.groupService.findById(id));
        systemLogService.log("get group info", this.request.getRequestURL().toString());
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
                groupService.deleteById(id);
            }catch (Exception e) {
                // TODO: handle exception
            }
        }
        systemLogService.log("group delete", this.request.getRequestURL().toString());
        return resultData;
    }

}
