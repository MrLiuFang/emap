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
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.event.HelpList;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.vo.EventRuleVo;
import com.pepper.model.emap.vo.HelpListVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.service.emap.event.EventRuleService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.node.NodeService;
import com.pepper.service.emap.node.NodeTypeService;
import com.pepper.util.MapToBeanUtil;

@Controller
@RequestMapping(value = "/front/event/rule")
@Validated
public class EventRuleController extends BaseControllerImpl implements BaseController {

	@Reference
	private EventRuleService eventRuleService;
	
	@Reference
	private NodeService nodeService;
	
	
	@Reference
	private NodeTypeService nodeTypeService;
	
	@Reference
	private DepartmentService departmentService;
	
	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String nodeId,Integer warningLevel) {
		Pager<EventRule> pager = new Pager<EventRule>();
		if(StringUtils.hasText(nodeId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_nodeId",nodeId);
		}
		if(warningLevel!=null) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_warningLevel",warningLevel);
		}
		pager = eventRuleService.findNavigator(pager);
		List<EventRule> list = pager.getResults();
		List<EventRuleVo> returnList = new ArrayList<EventRuleVo>();
		for(EventRule eventRule : list ) {
			returnList.add(this.convertEventRule(eventRule));
		}
		pager.setData("eventRule",returnList);
		pager.setResults(null);
		systemLogService.log("get event rule list", this.request.getRequestURL().toString());
		return pager;
	}
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		EventRule eventRule = new EventRule();
		MapToBeanUtil.convert(eventRule, map);
		
		EventRule oldEventRule = this.eventRuleService.findByNodeId(eventRule.getNodeId());
		if(oldEventRule!=null) {
			resultData.setMessage(Internationalization.getMessageInternationalization(5000001));
			resultData.setCode(5000001);
			return resultData;
		}
		
		Node node = this.nodeService.findById(eventRule.getNodeId());
		eventRule.setNodeTypeId(node.getNodeTypeId());
		eventRuleService.save(eventRule);
		systemLogService.log("event rule add", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		EventRule eventRule = new EventRule();
		MapToBeanUtil.convert(eventRule, map);
		
		EventRule oldEventRule = this.eventRuleService.findByNodeId(eventRule.getNodeId());
		if(!oldEventRule.getId().equals(eventRule.getId())) {
			resultData.setMessage(Internationalization.getMessageInternationalization(5000001));
			resultData.setCode(5000001);
			return resultData;
		}
		
		eventRuleService.update(eventRule);
		systemLogService.log("event rule update", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		EventRule eventRule = eventRuleService.findById(id);
		resultData.setData("eventRule",convertEventRule(eventRule));
		systemLogService.log("get event rule info", this.request.getRequestURL().toString());
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
				eventRuleService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("event rule delete", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private EventRuleVo convertEventRule(EventRule eventRule) {
		EventRuleVo  eventRuleVo = new EventRuleVo();
		if(eventRule!=null) {
			BeanUtils.copyProperties(eventRule, eventRuleVo);
			eventRuleVo.setNode(nodeService.findById(eventRule.getNodeId()));
			if(StringUtils.hasText(eventRuleVo.getDepartmentId())) {
				Department department = departmentService.findById(eventRuleVo.getDepartmentId());
				eventRuleVo.setDepartment(department);
			}
		}
		return eventRuleVo;
	}
}
