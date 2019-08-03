package com.pepper.controller.emap.front.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
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
import com.pepper.model.emap.event.HelpList;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.vo.HelpListVo;
import com.pepper.model.emap.vo.NodeVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.event.HelpListService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.node.NodeTypeService;
import com.pepper.util.MapToBeanUtil;

@Controller
@RequestMapping(value = "/front/help")
@Validated
public class HelpListController extends BaseControllerImpl implements BaseController {

	@Reference
	private HelpListService helpListService;
	
	@Reference
	private NodeTypeService nodeTypeService;
	
	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String code,String name,String nodeTypeId,Integer warningLevel,String keyWord) {
		Pager<HelpList> pager = new Pager<HelpList>();
		if(StringUtils.hasText(code)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_code",code);
		}
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name);
		}
		if(StringUtils.hasText(nodeTypeId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_nodeTypeId",nodeTypeId);
		}
		if(warningLevel!=null) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_warningLevel",warningLevel);
		}
		
		if(StringUtils.hasText(keyWord)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.OR_LIKE+"_name&code",keyWord );
		}
		pager = helpListService.findNavigator(pager);
		List<HelpList> list = pager.getResults();
		List<HelpListVo> returnList = new ArrayList<HelpListVo>();
		for(HelpList helpList : list ) {
			returnList.add(this.convertHelpList(helpList));
		}
		pager.setData("help",returnList);
		pager.setResults(null);
		systemLogService.log("get help list", this.request.getRequestURL().toString());
		return pager;
	}
	
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		HelpList helpList = new HelpList();
		MapToBeanUtil.convert(helpList, map);
		helpListService.save(helpList);
		systemLogService.log("help list add", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		HelpList helpList = new HelpList();
		MapToBeanUtil.convert(helpList, map);
		helpListService.update(helpList);
		systemLogService.log("help list update", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		HelpList helpList = helpListService.findById(id);
		if(helpList!=null) {
			resultData.setData("help",this.convertHelpList(helpList));
		}
		systemLogService.log("get help list info", this.request.getRequestURL().toString());
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
				helpListService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("help list delete", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private HelpListVo convertHelpList(HelpList helpList) {
		HelpListVo  helpListVo = new HelpListVo();
		if(helpList!=null) {
			BeanUtils.copyProperties(helpList, helpListVo);
			helpListVo.setNodeType(nodeTypeService.findById(helpList.getNodeTypeId()));
		}
		return helpListVo;
	}
}
