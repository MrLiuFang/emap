package com.pepper.controller.emap.front.node;

import com.pepper.controller.emap.core.ResultData;
import com.pepper.model.emap.node.NodeGroup;
import com.pepper.service.emap.node.NodeGroupService;
import groovy.util.IFileNameFinder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: 雄大
 * @create: 2021-02-04 15:49
 */
@RestController
@RequestMapping(value = "/front/nodeGroup")
public class NodeGroupController {

    @Autowired
    private NodeGroupService nodeGroupService;

    @RequestMapping("/addOrUpdateOrDelete")
    public Object add(@RequestBody NodeGroupDto nodeGroupDto){
        List<NodeGroup> list = new ArrayList<NodeGroup>();
        if (StringUtils.hasText(nodeGroupDto.getCode())){
            nodeGroupService.deleteAllByCode(nodeGroupDto.getCode());
        }
        nodeGroupDto.getNodeIds().forEach(s -> {
            NodeGroup nodeGroup = new NodeGroup();
            nodeGroup.setCode(nodeGroupDto.getCode());
            nodeGroup.setName(nodeGroupDto.getName());
            nodeGroup.setNodeId(s);
            list.add(nodeGroup);
        });
        if (list.size()>0) {
            nodeGroupService.saveAll(list);
        }
        return  new ResultData();
    }

    @RequestMapping("/toEdit")
    public Object toEdit(String code){
        ResultData resultData = new ResultData();
        resultData.setData("nodeGroup",nodeGroupService.find(code));
        return resultData;
    }

}
@Data
class NodeGroupDto{

    private String code;

    private String name;

    private List<String> nodeIds;
}


