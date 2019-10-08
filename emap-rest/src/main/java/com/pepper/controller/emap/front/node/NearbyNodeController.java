package com.pepper.controller.emap.front.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.node.NearbyNode;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.vo.NearbyNodeVo;
import com.pepper.model.emap.vo.NodeTypeVo;
import com.pepper.model.emap.vo.NodeVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.node.NearbyNodeService;
import com.pepper.service.emap.node.NodeService;
import com.pepper.service.emap.node.NodeTypeService;
import com.pepper.service.file.FileService;

@Controller()
@RequestMapping(value = "/front/nearbyNode")
public class NearbyNodeController extends BaseControllerImpl  implements BaseController {

	@Reference
	private NearbyNodeService nearbyNodeService;
	
	@Reference
	private NodeService nodeService;
	
	@Reference
	private NodeTypeService nodeTypeService;
	
	@Reference
	private FileService fileService;
	
	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String nodeId) {
		Pager<NearbyNode> pager = new Pager<NearbyNode>();
		ResultData resultData = new ResultData();
		Map<String,Object> searchParameter = new HashMap<String,Object>();
		pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_nodeId",nodeId);
		List<NearbyNode> list = nearbyNodeService.findNavigator(pager).getResults();
		List<String> ids = new ArrayList<String>(); 
		for(NearbyNode nearbyNode : list) {
			ids.add(nearbyNode.getNearbyNodeId());
		}
		NearbyNodeVo nearbyNodeVo = new NearbyNodeVo();
		Node node = nodeService.findById(nodeId);
		if(node!=null) {
			NodeVo  nodeVo = new NodeVo();
			BeanUtils.copyProperties(node, nodeVo);
			if(node.getStatus()!=null) {
				nodeVo.setStatusCode(node.getStatus().getName());
			}
			NodeTypeVo nodeTypeVo = new NodeTypeVo();
			NodeType nodeType = nodeTypeService.findById(node.getNodeTypeId());
			BeanUtils.copyProperties(nodeType, nodeTypeVo);
			nodeTypeVo.setWorkingIconUrl(fileService.getUrl(nodeType.getWorkingIcon()));
			nodeTypeVo.setProcessingIconUrl(fileService.getUrl(nodeType.getProcessingIcon()));
			nodeTypeVo.setStopIconUrl(fileService.getUrl(nodeType.getStopIcon()));
			nodeVo.setNodeType(nodeTypeVo);
			nearbyNodeVo.setNode(nodeVo);
		}
		if(ids.size()>0) {
			searchParameter.clear();
			searchParameter.put(SearchConstant.IN+"_id", ids);
			List<Node> listNode = nodeService.findAll(searchParameter);
			List<NodeVo> listNodeVo = new ArrayList<NodeVo>();
			for(Node obj : listNode) {
				NodeVo  nodeVo = new NodeVo();
				if(obj.getStatus()!=null) {
					nodeVo.setStatusCode(obj.getStatus().getName());
				}
				BeanUtils.copyProperties(obj, nodeVo);
				NodeTypeVo nodeTypeVo = new NodeTypeVo();
				NodeType nodeType = nodeTypeService.findById(obj.getNodeTypeId());
				BeanUtils.copyProperties(nodeType, nodeTypeVo);
				nodeTypeVo.setWorkingIconUrl(fileService.getUrl(nodeType.getWorkingIcon()));
				nodeTypeVo.setStopIconUrl(fileService.getUrl(nodeType.getStopIcon()));
				nodeVo.setNodeType(nodeTypeVo);
				listNodeVo.add(nodeVo);
			}
			nearbyNodeVo.setNearbyNode(listNodeVo);
		}
		resultData.setData(nearbyNodeVo);
		systemLogService.log("get nearby node list", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody String str) throws IOException {
		ResultData resultData = new ResultData();
		JsonNode jsonNode = new ObjectMapper().readTree(str);
		if(!jsonNode.has("nodeId")) {
			return resultData;
		}
		
		ArrayNode arrayNode = (ArrayNode)jsonNode.get("data");
		List<NearbyNode> list ;
		Map<String,List<NearbyNode>> map = new HashMap<String,List<NearbyNode>>();
		for(int i = 0; i <arrayNode.size(); i++) {
			String nearbyNodeId = arrayNode.get(i).asText();
			NearbyNode nearbyNode = new NearbyNode();
			nearbyNode.setNodeId(jsonNode.get("nodeId").asText());
			nearbyNode.setNearbyNodeId(nearbyNodeId);
			list = map.get(jsonNode.get("nodeId").asText());
			if(list==null) {
				list = new ArrayList<NearbyNode>();
			}
			list.add(nearbyNode);
			map.put(jsonNode.get("nodeId").asText(), list);
		}
		nearbyNodeService.deleteByNodeId(jsonNode.get("nodeId").asText());
		for (List<NearbyNode> v : map.values()) {
			for(NearbyNode nearbyNode : v) {
				nearbyNodeService.save(nearbyNode);
			}
		}
		systemLogService.log("nearby node add", this.request.getRequestURL().toString());
		return resultData;
	}
}
