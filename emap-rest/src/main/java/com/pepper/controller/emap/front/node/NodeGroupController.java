package com.pepper.controller.emap.front.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.Pager;
import com.pepper.core.exception.BusinessException;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeGroup;
import com.pepper.service.emap.node.NodeGroupService;
import com.pepper.service.emap.node.NodeService;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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
        AtomicInteger i = new AtomicInteger();
        nodeGroupDto.getNodeIds().forEach(ids -> {
            NodeGroup nodeGroup = new NodeGroup();
            nodeGroup.setCode(nodeGroupDto.getCode());
            nodeGroup.setName(nodeGroupDto.getName());
            nodeGroup.setIsMaster(Objects.equals(ids.getIsMaster(),true));
            if (Objects.equals(ids.getIsMaster(),true)){
                i.set(i.getAndIncrement()+1);
                NodeGroup tmp = nodeGroupService.find(ids.getId(),true);
                if (Objects.nonNull(tmp)){
                    Node node = nodeService.findById(ids.getId());
                    new BusinessException(node.getName()+"("+node.getSource()+")在其它联动组已是主设备");
                }
            }
            nodeGroup.setNodeId(ids.getId());
            list.add(nodeGroup);
        });
        if (i.getAndIncrement()>1){
            new BusinessException("联动组不能有两个/以上主设备");
        }
        if (i.getAndIncrement()<=0){
            new BusinessException("请选择主设备");
        }
        if (list.size()>0) {
            nodeGroupService.saveAll(list);
        }
        return  new ResultData();
    }

    @RequestMapping("/list")
    public Object list(String code,String name){
        ResultData resultData = new ResultData();
        Pager pager = new Pager();
        pager = nodeGroupService.findNavigator(pager,code,name);
        List<NodeGroup>  list  = pager.getResults();
        List<PageNodeGroupVo> returnList = new ArrayList<>();
        list.forEach(g->{
            PageNodeGroupVo groupVo = new PageNodeGroupVo();
            BeanUtils.copyProperties(g,groupVo);
            Node node = nodeService.findNodeGroupMaster(g.getCode());
            if (Objects.nonNull(node)){
                groupVo.setMasterNodeName(node.getName());
            }
            returnList.add(groupVo);
        });
        pager.setData("nodeGroup",returnList);
        pager.setResults(null);
        return pager;
    }

    @RequestMapping("/toEdit")
    public Object toEdit(String code){
        ResultData resultData = new ResultData();
        NodeGroupVo nodeGroupVo = new NodeGroupVo();
        List<NodeGroup> list = nodeGroupService.find(code);
        List<NodeGroupVo.NodeVo> nodes = new ArrayList<NodeGroupVo.NodeVo>();
        list.forEach(nodeGroup -> {
            nodeGroupVo.setCode(nodeGroup.getCode());
            nodeGroupVo.setName(nodeGroup.getName());
            Node node = nodeService.findById(nodeGroup.getNodeId());
            NodeGroupVo.NodeVo nodeVo = new NodeGroupVo.NodeVo();
            BeanUtils.copyProperties(node,nodeVo);
            nodeVo.setIsMaster(nodeGroup.getIsMaster());
            nodes.add(nodeVo);
        });
        nodeGroupVo.setNodes(nodes);
        resultData.setData("nodeGroup",nodeGroupVo);
        return resultData;
    }

}

@Data
class PageNodeGroupVo{

    private String code;

    private String name;

    private String masterNodeName;
}

@Data
class NodeGroupDto{

    private String code;

    private String name;

    private List<NodeIds> nodeIds;

    @Data
    public static class NodeIds {

        private String id;
        private Boolean isMaster;
    }

}

@Data@JsonIgnoreProperties({"isMaster","id","nodeId","createDate","updateDate","createUser","updateUser"})
class NodeGroupVo extends NodeGroup{
    private String code;

    private String name;

    private List<NodeVo> nodes;

    @Data
    public static class NodeVo extends Node{
        private Boolean isMaster;
    }
}

