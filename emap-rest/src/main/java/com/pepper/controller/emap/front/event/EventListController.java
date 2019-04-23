package com.pepper.controller.emap.front.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
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
import com.pepper.core.ResultEnum.Status;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.vo.EventListVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.event.EventDispatchService;
import com.pepper.service.emap.event.EventListService;
import com.pepper.service.emap.event.HelpListService;
import com.pepper.service.emap.node.NodeService;

@Controller
@RequestMapping("/front/event")
public class EventListController extends BaseControllerImpl implements BaseController {

	@Reference
	private EventListService eventListService;
	
	@Reference
	private EventDispatchService eventDispatchService;
	
	@Resource
	private Environment environment;
	
	@Resource
	private NodeService nodeService;
	
	@Resource
	private HelpListService helpListService;
	
	@RequestMapping("/workbench/list")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object list(Boolean isUrgent) {
		Pager<EventList> pager = new Pager<EventList>();
		if(isUrgent) {
			pager = eventListService.List(pager, Integer.valueOf(environment.getProperty("warningLevel", "0")),isUrgent);
		}else {
			pager = eventListService.List(pager, null,isUrgent);
		}
		List<EventList> list = pager.getResults();
		pager.setResults(null);
		pager.setData("eventList",convertVo(list));
		return pager;
	}
	
	@RequestMapping("/workbench/automatic/list")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object automaticList() {
		Pager<EventList> pager = new Pager<EventList>();
		pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_status", "A");
		eventListService.findNavigator(pager).getResults();
		List<EventList> list = pager.getResults();
		pager.setResults(null);
		pager.setData("eventList",convertVo(list));
		return pager;
		
	}
	
	
	
	@RequestMapping("/workbench/toMe")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public synchronized Object eventToMe(@RequestBody String id) throws IOException {
		AdminUser currentUser = (AdminUser) this.getCurrentUser();
		ResultData resultData = new ResultData();
		if(!StringUtils.hasText(id)){
			return resultData;
		}
		JsonNode jsonNode = new ObjectMapper().readTree(id);
		if(!jsonNode.has("id")) {
			return resultData;
		}
		ArrayNode arrayNode = (ArrayNode)jsonNode.get("id");
		for(int i = 0; i <arrayNode.size(); i++) {
			EventList eventList = eventListService.findById(arrayNode.get(i).asText());
			if(StringUtils.hasText(eventList.getStatus())){
				resultData.setMessage(new String("操作失败！("+eventList.getEventName()+")被其它用户以处理！"));
				resultData.setStatus(Status.LOGIC_ERROR.getKey());
				return resultData;
			}
		}
		
		for(int i = 0; i <arrayNode.size(); i++) {
			EventList eventList = eventListService.findById(arrayNode.get(i).asText());
			eventList.setStatus("N");
			eventList.setOperator(currentUser.getId());
			eventListService.update(eventList);
		}
		
		return resultData;
	}
	
	@RequestMapping("/workbench/forMe")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object eventForMe() {
		AdminUser currentUser = (AdminUser) this.getCurrentUser();
		Pager<EventList> pager = new Pager<EventList>();
		pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_operator", currentUser.getId());
		eventListService.findNavigator(pager).getResults();
		List<EventList> list = pager.getResults();
		pager.setResults(null);
		pager.setData("eventList",convertVo(list));
		return pager;
	}
	
	@RequestMapping("/workbench/eventHelp")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object eventHelp(String id) {
		ResultData resultData = new ResultData();
		Node node = nodeService.findById(id);
		if(node!=null && StringUtils.hasText(node.getNodeTypeId())) {
			resultData.setData("helpList", helpListService.findByNodeTypeId(node.getNodeTypeId()));
		}
		return resultData;
	}
	
	private List<EventListVo> convertVo(List<EventList> list){
		List<EventListVo> returnList  = new ArrayList<EventListVo>();
		for(EventList eventList : list) {
			EventListVo eventListVo = new EventListVo();
			BeanUtils.copyProperties(eventList, eventListVo);
			eventListVo.setNode(nodeService.findBySourceCode(eventList.getSourceCode()));
			returnList.add(eventListVo);
		}
		return returnList;
	}
}
