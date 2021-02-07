package com.pepper.controller.emap.front.node;

import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.Pager;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeClassify;
import com.pepper.model.emap.node.NodeGroup;
import com.pepper.service.emap.node.NodeGroupService;
import com.pepper.service.emap.node.NodeService;
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
import java.util.Objects;

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

    @Autowired
    private NodeService nodeService;

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

    @RequestMapping("/list")
    public Object list(String code,String name){
        ResultData resultData = new ResultData();
        Pager<NodeGroup> pager = new Pager<NodeGroup>();
        pager = nodeGroupService.findNavigator(pager,code,name);
        pager.setData("nodeGroup",pager.getResults());
        pager.setResults(null);
        return pager;
    }

    @RequestMapping("/toEdit")
    public Object toEdit(String code){
        ResultData resultData = new ResultData();
        NodeGroupVo nodeGroupVo = new NodeGroupVo();
        List<NodeGroup> list = nodeGroupService.find(code);
        List<Node> nodes = new ArrayList<Node>();
        list.forEach(nodeGroup -> {
            nodeGroupVo.setCode(nodeGroup.getCode());
            nodeGroupVo.setName(nodeGroup.getName());
            Node node = nodeService.findById(nodeGroup.getNodeId());
            nodes.add(node);
        });
        nodeGroupVo.setNodes(nodes);
        resultData.setData("nodeGroup",nodeGroupVo);
        return resultData;
    }

}
@Data
class NodeGroupDto{

    private String code;

    private String name;

    private List<String> nodeIds;
}
@Data
class NodeGroupVo extends NodeGroup{
    private String code;

    private String name;

    private List<Node> nodes;
}

