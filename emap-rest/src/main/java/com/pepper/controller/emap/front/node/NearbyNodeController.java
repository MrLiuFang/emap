package com.pepper.controller.emap.front.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
		Pager<NearbyNode> pager = new Pager<NearbyNode>();
		if(StringUtils.hasText(nodeId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_nodeId",nodeId );
		}
		
		pager = nearbyNodeService.findNavigator(pager);
		List<NearbyNode> list = pager.getResults();
		List<NearbyNodeVo> returnList = new ArrayList<NearbyNodeVo>();
		for(NearbyNode nearbyNode : list) {
			NearbyNodeVo nearbyNodeVo = new NearbyNodeVo();
			nearbyNodeVo.setNode(nodeService.findById(nearbyNode.getNodeId()));
			returnList.add(nearbyNodeVo);
		}
		
		pager.setData("node",returnList);
		pager.setResults(null);
		return pager;
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
		for(int i = 0; i <arrayNode.size(); i++) {
			String nearbyNodeId = arrayNode.get(i).asText();
			NearbyNode nearbyNode = new NearbyNode();
			nearbyNode.setNodeId(jsonNode.get("nodeId").asText());
			nearbyNode.setNearbyNodeId(nearbyNodeId);
			nearbyNodeService.save(nearbyNode);
		}
		return resultData;
	}
}
