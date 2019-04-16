package com.pepper.controller.emap.front.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.node.NearbyNode;
import com.pepper.model.emap.vo.NearbyNodeVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.node.NearbyNodeService;
import com.pepper.service.emap.node.NodeService;

@Controller()
@RequestMapping(value = "/front/nearbyNode")
public class NearbyNodeController extends BaseControllerImpl  implements BaseController {

	@Reference
	private NearbyNodeService nearbyNodeService;
	
	@Reference
	private NodeService nodeService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String nodeId) {
		ResultData resultData = new ResultData();
		Map<String,Object> searchParameter = new HashMap<String,Object>();
		searchParameter.put(SearchConstant.EQUAL+"_nodeId",nodeId);
		List<NearbyNode> list = nearbyNodeService.findAll(searchParameter);
		List<String> ids = new ArrayList<String>(); 
		for(NearbyNode nearbyNode : list) {
			ids.add(nearbyNode.getNearbyNodeId());
		}
		NearbyNodeVo nearbyNodeVo = new NearbyNodeVo();
		nearbyNodeVo.setNode(nodeService.findById(nodeId));
		if(ids.size()>0) {
			searchParameter.clear();
			searchParameter.put(SearchConstant.IN+"_id", ids);
			nearbyNodeVo.setNearbyNode(nodeService.findAll(searchParameter));
		}
		resultData.setData(nearbyNodeVo);
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
		return resultData;
	}
}
