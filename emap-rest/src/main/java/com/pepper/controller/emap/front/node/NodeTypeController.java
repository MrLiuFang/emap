package com.pepper.controller.emap.front.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.vo.NodeTypeVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.node.NodeTypeService;
import com.pepper.service.file.FileService;
import com.pepper.util.MapToBeanUtil;

@Controller()
@RequestMapping(value = "/front/nodeType")
public class NodeTypeController extends BaseControllerImpl implements BaseController {

	@Reference
	private NodeTypeService nodeTypeService;
	
	@Reference
	private FileService fileService;
	
	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String code,String name,String keyWord) {
		Pager<NodeType> pager = new Pager<NodeType>();
		if(StringUtils.hasText(code)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_code",code );
		}
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
		}
		if(StringUtils.hasText(keyWord)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.OR_LIKE+"_code&name",keyWord );
		}
		pager = nodeTypeService.findNavigator(pager);
		List<NodeType> list = pager.getResults();
		List<NodeTypeVo> returnList = new ArrayList<NodeTypeVo>();
		for(NodeType nodeType : list) {
			returnList.add(convertNodeTypeVo(nodeType));
		}
		
		pager.setData("nodeType",returnList);
		pager.setResults(null);
		systemLogService.log("get node type list", this.request.getRequestURL().toString());
		return pager;
	}
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		NodeType nodeType = new NodeType();
		MapToBeanUtil.convert(nodeType, map);
		
		if(nodeTypeService.findByCode(nodeType.getCode())!=null) {
			resultData.setCode(2000001);
			resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
			return resultData;
		}
		nodeTypeService.save(nodeType);
		systemLogService.log("node type add", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		NodeType nodeType = new NodeType();
		MapToBeanUtil.convert(nodeType, map);
		
		NodeType oldNodeType = nodeTypeService.findByCode(nodeType.getCode());
		if(oldNodeType!=null && oldNodeType.getCode()!=null&&nodeType.getCode()!=null) {
			if(!nodeType.getId().equals(oldNodeType.getId())){
				resultData.setCode(2000001);
				resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
				return resultData;
			}
		}
		
		nodeTypeService.update(nodeType);
		systemLogService.log("node type update", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		resultData.setData("nodeType",convertNodeTypeVo(nodeTypeService.findById(id)));
		systemLogService.log("get node type info", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/delete")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object delete(@RequestBody String str) throws IOException {
		ResultData resultData = new ResultData();
		if(!StringUtils.hasText(str)){
			return resultData;
		}
		JsonNode jsonNode = new ObjectMapper().readTree(str);
		if(!jsonNode.has("id")) {
			return resultData;
		}
		ArrayNode arrayNode = (ArrayNode)jsonNode.get("id");
		for(int i = 0; i <arrayNode.size(); i++) {
			String id = arrayNode.get(i).asText();
			try {
				nodeTypeService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("node type delete", this.request.getRequestURL().toString());
		return resultData;
	}
	
	/**
	 * 
	 * @param nodeType
	 * @return
	 */
	private NodeTypeVo convertNodeTypeVo(NodeType nodeType) {
		if(nodeType == null) {
			return null;
		}
		NodeTypeVo  nodeTypeVo = new NodeTypeVo();
		BeanUtils.copyProperties(nodeType, nodeTypeVo);
		nodeTypeVo.setWorkingIconUrl(fileService.getUrl(nodeType.getWorkingIcon()));
		nodeTypeVo.setStopIconUrl(fileService.getUrl(nodeType.getStopIcon()));
		return nodeTypeVo;
	}

}
