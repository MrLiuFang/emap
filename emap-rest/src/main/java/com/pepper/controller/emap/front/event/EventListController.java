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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import com.pepper.model.emap.event.EventDispatch;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.vo.EventListVo;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.model.emap.vo.NodeTypeVo;
import com.pepper.model.emap.vo.NodeVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.emap.event.EventDispatchService;
import com.pepper.service.emap.event.EventListService;
import com.pepper.service.emap.event.EventRuleService;
import com.pepper.service.emap.event.HelpListService;
import com.pepper.service.emap.map.MapImageUrlService;
import com.pepper.service.emap.map.MapService;
import com.pepper.service.emap.message.MessageService;
import com.pepper.service.emap.node.NodeService;
import com.pepper.service.emap.node.NodeTypeService;
import com.pepper.service.file.FileService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;
import com.pepper.util.MapToBeanUtil;

@Controller
@RequestMapping("/front/event")
public class EventListController extends BaseControllerImpl implements BaseController {

	@Reference
	private EventListService eventListService;
	
	@Reference
	private EventRuleService eventRuleService;
	
	@Reference
	private EventDispatchService eventDispatchService;
	
	@Resource
	private Environment environment;
	
	@Resource
	private NodeService nodeService;
	
	@Resource
	private HelpListService helpListService;
	
	@Resource
	private MapService mapService;
	
	@Resource
	private MapImageUrlService mapImageUrlService;
	
	@Resource
	private NodeTypeService nodeTypeService;
	
	@Resource
	private AdminUserService adminUserService;
	
	@Resource
	private FileService fileService;
	
	@Reference
	private ValueOperationsService valueOperationsService;
	
	@Reference
	private MessageService messageService;
	
	@RequestMapping(value = "/add")
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		EventList eventList = new EventList();
		MapToBeanUtil.convert(eventList, map);
		eventList.setIsFiled(false);
		eventList.setIsOperatorTransfer(false);
		eventListService.save(eventList);
		return resultData;
	}
	
	@RequestMapping("/workbench/list")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object list(Boolean isUrgent,String id) {
		Pager<EventList> pager = new Pager<EventList>();
		pager = eventListService.List(pager, Integer.valueOf(environment.getProperty("warningLevel", "0")),isUrgent,id);
		List<EventList> list = pager.getResults();
		pager.setResults(null);
		pager.setData("eventList",convertVo(list));
		return pager;
	}
	
	@RequestMapping("/workbench/automatic/list")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object automaticList(String id) {
		Pager<EventList> pager = new Pager<EventList>();
		pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_status", "A");
		if(StringUtils.hasText(id)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_id", id);
		}
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
			if(StringUtils.hasText(eventList.getOperator())){
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
	
	@RequestMapping("/workbench/forMe/handle")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object eventForMeHandle(Boolean isHandle,String id) {
		return eventForMeEx(true,id);
	}
	
	@RequestMapping("/workbench/forMe/noHandle")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object eventForMeNoHandle(Boolean isHandle,String id) {
		return eventForMeEx(false,id);
	}
	
	private Pager<EventList> eventForMeEx(Boolean isHandle,String id){
		AdminUser currentUser = (AdminUser) this.getCurrentUser();
		Pager<EventList> pager = new Pager<EventList>();
		pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_operator", currentUser.getId());
		if(isHandle) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.ISNOTNULL+"_currentHandleUser",null );
		}else {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.ISNULL+"_currentHandleUser", null);
		}
		if(StringUtils.hasText(id)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_id", id);
		}
		pager.getJpqlParameter().setSearchParameter(SearchConstant.NOTEQUAL+"_isFiled", true);
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
	
	@RequestMapping("/workbench/getEmployee")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object  getEmployee(String id) {
		ResultData resultData = new ResultData();
		EventList eventList = eventListService.findById(id);
		if(eventList == null) {
			return resultData;
		}
		Node node = nodeService.findBySourceCode(eventList.getSourceCode());
		if(node==null) {
			return resultData;
		}
		EventRule eventRule = eventRuleService.findByNodeId(node.getId());
		if(eventRule == null || !StringUtils.hasText(eventRule.getDepartmentId())) {
			resultData.setStatus(300001);
			resultData.setMessage("该设备未设备部门!");
		}
		resultData.setData("employee", adminUserService.findUserByDepartmentId(eventRule.getDepartmentId()));
		return resultData;
	}
	
	@RequestMapping("/workbench/toEmployee")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object toEmployee(@RequestBody String str) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,Object> map = objectMapper.readValue(str, Map.class);
//		String eventId,String employeeId,String helpId,String content 
		ResultData resultData = new ResultData();
		EventList eventList = this.eventListService.findById(map.get("eventId").toString());
		if(eventList==null) {
			return resultData;
		}
		eventList.setCurrentHandleUser(map.get("employeeId").toString());
		eventList.setStatus("W");
		if(map.containsKey("helpId")) {
			eventList.setHelpId(map.get("helpId").toString());
		}
		if(map.containsKey("content")) {
			eventList.setContent(map.get("content").toString());
		}
		eventListService.update(eventList);
		
		AdminUser adminuser =  (AdminUser) this.getCurrentUser();
		EventDispatch eventDispatch = new EventDispatch();
		eventDispatch.setEventId(eventList.getEventId());
		eventDispatch.setEventListId(eventList.getId());
		eventDispatch.setOperator(map.get("employeeId").toString());
		eventDispatch.setDispatchFrom(adminuser.getId());
		eventDispatch.setTitle(eventList.getEventName());
		eventDispatchService.save(eventDispatch);
		
		try {
			String employeeId = map.get("employeeId").toString();
			String deviceId = valueOperationsService.get("userDeviceId_"+employeeId);
			messageService.send(deviceId, "您有新的工单",eventList.getEventName(),eventList.getId());
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return resultData;
	}
	
	@RequestMapping("/workbench/historyEvent")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object historyEvent(String id) {
		ResultData resultData = new ResultData();
		EventList eventList = this.eventListService.findById(id);
		if(eventList==null) {
			return resultData;
		}
		List<EventList> list= this.eventListService.findBySourceCodeAndIdNot(eventList.getSourceCode(), eventList.getId());
		
		List<EventListVo> returnList  = new ArrayList<EventListVo>();
		for(EventList obj : list) {
			EventListVo eventListVo = new EventListVo();
			BeanUtils.copyProperties(obj, eventListVo);
			if(StringUtils.hasText(obj.getOperator())) {
				AdminUser operatorUser = this.adminUserService.findById(obj.getOperator());
				if(operatorUser!=null) {
					eventListVo.setOperatorVo(operatorUser);
				}
			}
			if(StringUtils.hasText(obj.getCurrentHandleUser())) {
				AdminUser currentHandleUser = this.adminUserService.findById(obj.getCurrentHandleUser());
				if(currentHandleUser!=null) {
					eventListVo.setCurrentHandleUserVo(currentHandleUser);
				}
			}
			returnList.add(eventListVo);
		}
		
		resultData.setData("historyEvent", returnList);
		return resultData;
	}
	
	@RequestMapping("/workbench/filed")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object filed(@RequestBody Map<String,String> map) {
		ResultData resultData = new ResultData();
		if(map.containsKey("id")) {
			EventList eventList = this.eventListService.findById(map.get("id"));
			eventList.setIsFiled(true);
			eventList.setFiledContent(map.get("filedContent"));
			eventListService.update(eventList);
		}
		
		
		return resultData;
	}
	
	@RequestMapping("/workbench/toOperator")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object toOprrator(@RequestBody Map<String,String> map) {
		ResultData resultData = new ResultData();
		String operatorId = map.get("operatorId").toString();
		String eventId =  map.get("eventId").toString();
		EventList eventList = this.eventListService.findById(eventId);
		if(eventList == null) {
			return resultData;
		}
		eventList.setOperator(operatorId);
		eventList.setIsOperatorTransfer(true);
		eventList.setOperatorTransferRead(false);
		eventListService.update(eventList);
		return resultData;
	}
	
	@RequestMapping("/workbench/toOperator/roundRobin")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object toOprratorRoundRobin() {
		ResultData resultData = new ResultData();
		AdminUser user = (AdminUser) this.getCurrentUser();
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		searchParameter.put(SearchConstant.EQUAL+"_operator", user.getId());
		searchParameter.put(SearchConstant.EQUAL+"_isOperatorTransfer", true);
		searchParameter.put(SearchConstant.EQUAL+"_operatorTransferRead", false);
		
		List<EventList> list =  this.eventListService.findAll(searchParameter);
		for(EventList eventList : list) {
			eventList.setOperatorTransferRead(true);
			eventListService.update(eventList);
		}
		resultData.setData("eventList", list);
		return resultData;
	}
	
	
		
	private List<EventListVo> convertVo(List<EventList> list){
		List<EventListVo> returnList  = new ArrayList<EventListVo>();
		for(EventList eventList : list) {
			EventListVo eventListVo = new EventListVo();
			BeanUtils.copyProperties(eventList, eventListVo);
			Node node = nodeService.findBySourceCode(eventList.getSourceCode());
			if(node!=null) {
				NodeVo nodeVo = new NodeVo();
				BeanUtils.copyProperties(node, nodeVo);
				if(node.getStatus()!=null) {
					nodeVo.setStatusCode(node.getStatus().getName());
				}
				com.pepper.model.emap.map.Map map = mapService.findById(node.getMapId());
				if(map!=null) {
					MapVo mapVo = new MapVo();
					BeanUtils.copyProperties(map, mapVo);
					nodeVo.setMap(mapVo);
					NodeTypeVo nodeTypeVo = new NodeTypeVo();
					NodeType nodeType = nodeTypeService.findById(node.getNodeTypeId());
					if(nodeType != null) {
						BeanUtils.copyProperties(nodeType, nodeTypeVo);
						nodeTypeVo.setWorkingIconUrl(fileService.getUrl(nodeType.getWorkingIcon()));
						nodeTypeVo.setStopIconUrl(fileService.getUrl(nodeType.getStopIcon()));
						nodeVo.setNodeType(nodeTypeVo);
					}
					mapVo.setMapImageUrl(mapImageUrlService.findByMapId(map.getId()));
				}
				
				eventListVo.setNode(nodeVo);
			}
			if(StringUtils.hasText(eventList.getCurrentHandleUser()) ) {
				AdminUser currentHandleUser = this.adminUserService.findById(eventList.getCurrentHandleUser());
				eventListVo.setCurrentHandleUserVo(currentHandleUser);
			}
//			eventListVo.setHistoryEventList(this.eventListService.findBySourceCodeAndIdNot(eventList.getSourceCode(), eventList.getId()));
			returnList.add(eventListVo);
		}
		return returnList;
	}
}
