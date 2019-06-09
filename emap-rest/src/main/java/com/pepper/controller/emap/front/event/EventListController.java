package com.pepper.controller.emap.front.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.department.DepartmentGroup;
import com.pepper.model.emap.event.ActionList;
import com.pepper.model.emap.event.EventDispatch;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.event.HelpList;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.vo.ActionListVo;
import com.pepper.model.emap.vo.AdminUserVo;
import com.pepper.model.emap.vo.DepartmentGroupVo1;
import com.pepper.model.emap.vo.DepartmentVo1;
import com.pepper.model.emap.vo.EventListVo;
import com.pepper.model.emap.vo.HelpListVo;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.model.emap.vo.NodeTypeVo;
import com.pepper.model.emap.vo.NodeVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.emap.department.DepartmentGroupService;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.service.emap.event.ActionListService;
import com.pepper.service.emap.event.EventDispatchService;
import com.pepper.service.emap.event.EventListService;
import com.pepper.service.emap.event.EventRuleService;
import com.pepper.service.emap.event.HelpListService;
import com.pepper.service.emap.log.SystemLogService;
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
	
	@Reference
	private ActionListService actionListService;
	
	@Reference
	private SystemLogService systemLogService;
	
	@Reference
	private DepartmentService departmentService;
	
	@Reference
	private DepartmentGroupService departmentGroupService;
	
	@RequestMapping(value = "/add")
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		EventList eventList = new EventList();
		MapToBeanUtil.convert(eventList, map);
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
		systemLogService.log("get event list", this.request.getRequestURL().toString());
		return pager;
	}
	
	@RequestMapping("/workbench/automatic/list")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object automaticList(String id) {
		Pager<EventList> pager = new Pager<EventList>();
		pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_status", "A");
		pager.getJpqlParameter().setSearchParameter(SearchConstant.ISNULL+"_operator", null);
		if(StringUtils.hasText(id)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_id", id);
		}
		eventListService.findNavigator(pager).getResults();
		List<EventList> list = pager.getResults();
		pager.setResults(null);
		pager.setData("eventList",convertVo(list));
		systemLogService.log("get event automatic list", this.request.getRequestURL().toString());
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
			if(eventList.getStatus().equals("N")) {
				eventList.setStatus("W");
			}
			eventList.setOperator(currentUser.getId());
			eventListService.update(eventList);
		}
		systemLogService.log("event to me", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping("/workbench/forMe/handle")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object eventForMeHandle(Boolean isHandle,String id) {
		systemLogService.log("event for me handle", this.request.getRequestURL().toString());
		return eventForMeEx(true,id);
	}
	
	@RequestMapping("/workbench/forMe/noHandle")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object eventForMeNoHandle(Boolean isHandle,String id) {
		systemLogService.log("event for me no handle", this.request.getRequestURL().toString());
		return eventForMeEx(false,id);
	}
	
	private Pager<EventList> eventForMeEx(Boolean isHandle,String id){
		AdminUser currentUser = (AdminUser) this.getCurrentUser();
		Pager<EventList> pager = new Pager<EventList>();
		pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_operator", currentUser.getId());
		//处理中
		if(isHandle) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.IN+"_status", new String[] {"A","B"});
		}else {//未处理中
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_status", "W");
		}
		if(StringUtils.hasText(id)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_id", id);
		}
		eventListService.findNavigator(pager).getResults();
		List<EventList> list = pager.getResults();
		pager.setResults(null);
		pager.setData("eventList",convertVo(list));
		
		return pager;
	}
	
	@RequestMapping("/workbench/eventHelp")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object eventHelp(String id,String eventId) {
		ResultData resultData = new ResultData();
		Node node = nodeService.findById(id);
		EventList eventList = this.eventListService.findById(eventId);
		int warningLevel = 0;
		if(eventList!=null) {
			warningLevel=eventList.getWarningLevel();
		}
		if(node!=null && StringUtils.hasText(node.getNodeTypeId())) {
			resultData.setData("helpList", helpListService.findByNodeTypeIdAndWarningLevelGreaterThanEqual(node.getNodeTypeId(),warningLevel));
		}
		systemLogService.log("get event help", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping("/workbench/getEmployee")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object  getEmployee(String id) {
		ResultData resultData = new ResultData();
		List<Department> list =  departmentService.findAll();
		List<DepartmentVo1> returnList = new ArrayList<DepartmentVo1>();
		for(Department department : list) {
			DepartmentVo1 departmentVo1 = new DepartmentVo1();
			BeanUtils.copyProperties(department, departmentVo1);
			List<DepartmentGroup> listDepartmentGroup = departmentGroupService.findByDepartmentId(department.getId());
			List<DepartmentGroupVo1> listDepartmentGroupV1 = new ArrayList<DepartmentGroupVo1>();
			for(DepartmentGroup departmentGroup : listDepartmentGroup) {
				DepartmentGroupVo1 departmentGroupV1 = new DepartmentGroupVo1();
				BeanUtils.copyProperties(departmentGroup, departmentGroupV1);
				List<AdminUser> listAdminUser = this.adminUserService.findByDepartmentGroupId(departmentGroup.getId());
				List<AdminUserVo> listAdminUserVo = new ArrayList<AdminUserVo>();
				for(AdminUser adminUser : listAdminUser) {
					AdminUserVo adminUserVo = new AdminUserVo();
					BeanUtils.copyProperties(adminUser, adminUserVo);
					adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(adminUser.getHeadPortrait()));
					listAdminUserVo.add(adminUserVo);
				}
				listDepartmentGroupV1.add(departmentGroupV1);
			}
			departmentVo1.setDepartmentGroup(listDepartmentGroupV1);
			returnList.add(departmentVo1);
			
		}
		resultData.setData("department", returnList);
		return resultData;
	}
	
//	
	
	@RequestMapping("/workbench/retransmission")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object retransmission(@RequestBody String str) throws JsonParseException, JsonMappingException, IOException {
		
		systemLogService.log("event  retransmission", this.request.getRequestURL().toString());
		return toEmployeeEx(str);
	}

	
	@RequestMapping("/workbench/toEmployee")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object toEmployee(@RequestBody String str) throws JsonParseException, JsonMappingException, IOException {
		
		systemLogService.log("event to employee", this.request.getRequestURL().toString());
		return toEmployeeEx(str);
	}
	
	private ResultData toEmployeeEx(String str) throws JsonParseException, JsonMappingException, IOException  {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,Object> map = objectMapper.readValue(str, Map.class);
//		String eventId,String employeeId,String helpId,String content 
		ResultData resultData = new ResultData();
		EventList eventList = this.eventListService.findById(map.get("eventId").toString());
		if(eventList==null) {
			resultData.setCode(800001);
			resultData.setMessage("事件无效");
			return resultData;
		}
		if(!map.containsKey("employeeId")) {
			resultData.setCode(800002);
			resultData.setMessage("请选择工人");
			return resultData;
		}
		eventList.setCurrentHandleUser(map.get("employeeId").toString());
		eventList.setStatus("A");
		if(map.containsKey("helpId")) {
			eventList.setHelpId(map.get("helpId")==null?"[]":objectMapper.writeValueAsString(map.get("helpId")));
		}
		if(map.containsKey("content")) {
			eventList.setContent(map.get("content")==null?"":map.get("content").toString());
		}
		eventList.setAssignDate(new Date());
		eventListService.update(eventList);
		
		AdminUser adminuser =  (AdminUser) this.getCurrentUser();
		EventDispatch eventDispatch = new EventDispatch();
		eventDispatch.setEventId(eventList.getEventId());
		eventDispatch.setEventListId(eventList.getId());
		eventDispatch.setOperator(map.get("employeeId").toString());
		eventDispatch.setDispatchFrom(adminuser.getId());
		eventDispatch.setTitle(eventList.getEventName());
		eventDispatch.setAssignDate(new Date());
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
	public Object historyEvent(String id) throws IOException {
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
					AdminUserVo adminUserVo = new AdminUserVo();
					BeanUtils.copyProperties(operatorUser, adminUserVo);
					adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(operatorUser.getHeadPortrait()));
					eventListVo.setOperatorVo(adminUserVo);
				}
			}
			if(StringUtils.hasText(obj.getCurrentHandleUser())) {
				AdminUser currentHandleUser = this.adminUserService.findById(obj.getCurrentHandleUser());
				if(currentHandleUser!=null) {
					AdminUserVo adminUserVo = new AdminUserVo();
					BeanUtils.copyProperties(currentHandleUser, adminUserVo);
					adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(currentHandleUser.getHeadPortrait()));
					eventListVo.setCurrentHandleUserVo(adminUserVo);
				}
			}

			returnList.add(eventListVo);
		}
		resultData.setData("historyEvent", returnList);
		systemLogService.log("event history list", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping("/workbench/actionList")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object acctionList(String eventId) throws IOException {
		ResultData resultData = new ResultData();
		EventList eventList = this.eventListService.findById(eventId);
		List<ActionList> actionList = actionListService.findByEventListId(eventId);
		List<ActionListVo> actionListVo = new ArrayList<ActionListVo>();
		for(ActionList obj1 : actionList) {
			ActionListVo tmp = new ActionListVo();
			BeanUtils.copyProperties(obj1, tmp);
			tmp.setImageUrl1(this.fileService.getUrl(tmp.getImage1()));
			tmp.setImageUrl2(this.fileService.getUrl(tmp.getImage2()));
			tmp.setImageUrl3(this.fileService.getUrl(tmp.getImage3()));
			tmp.setVoiceUrl1(this.fileService.getUrl(tmp.getVoice1()));
			Node node = nodeService.findBySourceCode(eventList.getSourceCode());
			if(node!=null && StringUtils.hasText(node.getNodeTypeId())) {
				tmp.setHelpList(convertHelpList(helpListService.findByNodeTypeId(node.getNodeTypeId()),obj1.getHelpId(),obj1.getOperatorHelpId()));
			}
			if(StringUtils.hasText(obj1.getOperator())) {
				AdminUser operatorUser = this.adminUserService.findById(obj1.getOperator());
				if(operatorUser!=null) {
					AdminUserVo adminUserVo = new AdminUserVo();
					BeanUtils.copyProperties(operatorUser, adminUserVo);
					adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(operatorUser.getHeadPortrait()));
					adminUserVo.setPassword("");
					tmp.setEmployee(adminUserVo);
				}
			}
			actionListVo.add(tmp);
		}
		resultData.setData("actionList", actionListVo);
		return resultData;
	}
	
	private List<HelpListVo> convertHelpList(List<HelpList> helpList,String helpId,String helpId1) throws IOException{
		List<HelpListVo> helpListVo = new ArrayList<HelpListVo>();
		List<String> helpIdList = new ArrayList<String>();
		ObjectMapper objectMapper = new ObjectMapper();
		if(StringUtils.hasText(helpId)) {
			try{
				JsonNode jsonNode = objectMapper.readTree(helpId);
				if(jsonNode.isArray()) {
					Iterator<JsonNode> array =jsonNode.iterator();
					while (array.hasNext()) {
						helpIdList.add(array.next().asText());
					}
				}
			}catch (Exception e) {
				
			}
		}
		
		List<String> listHelpId = new ArrayList<String>();
		if(StringUtils.hasText(helpId1)) {
			JsonNode jsonNode = objectMapper.readTree(helpId1);
			if(jsonNode.isArray()) {
				Iterator<JsonNode> array =jsonNode.iterator();
				while (array.hasNext()) {
					listHelpId.add(array.next().asText());
				}
			}
		}
		
		for(HelpList obj : helpList) {
			HelpListVo obj1 = new HelpListVo();
			BeanUtils.copyProperties(obj,obj1);
			obj1.setIsCheck(helpIdList.contains(obj.getId()));
			if(listHelpId.size()>0) {
				if(listHelpId.contains(obj.getId())) {
					helpListVo.add(obj1);
				}
			}else {
				helpListVo.add(obj1);
			}
		}
		return helpListVo;
	}
	
	@RequestMapping("/workbench/filed")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object filed(@RequestBody Map<String,String> map) {
		ResultData resultData = new ResultData();
		if(map.containsKey("id")) {
			EventList eventList = this.eventListService.findById(map.get("id"));
			eventList.setStatus("P");
			eventList.setFiledContent(map.get("filedContent"));
			eventListService.update(eventList);
		}
		systemLogService.log("event filed", this.request.getRequestURL().toString());
		
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
		eventList.setOperatorTransferDate(new Date());
		eventListService.update(eventList);
		systemLogService.log("event to operator", this.request.getRequestURL().toString());
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
		systemLogService.log("event to operator round robin", this.request.getRequestURL().toString());
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
				if(currentHandleUser!=null) {
					AdminUserVo adminUserVo = new AdminUserVo();
					BeanUtils.copyProperties(currentHandleUser, adminUserVo);
					adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(currentHandleUser.getHeadPortrait()));
					eventListVo.setCurrentHandleUserVo(adminUserVo);
				}
			}
			
			returnList.add(eventListVo);
		}
		return returnList;
	}
	
}
