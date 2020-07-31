package com.pepper.controller.emap.front.event;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import com.pepper.core.ResultEnum;
import com.pepper.model.console.role.Role;
import com.pepper.service.console.role.RoleService;
import org.apache.dubbo.config.annotation.Reference;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.pepper.controller.emap.util.Internationalization;
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
import com.pepper.model.emap.event.EventListAssist;
import com.pepper.model.emap.event.EventMessage;
import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.event.HelpList;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.staff.Staff;
import com.pepper.model.emap.vo.ActionListVo;
import com.pepper.model.emap.vo.AdminUserVo;
import com.pepper.model.emap.vo.DepartmentGroupVo1;
import com.pepper.model.emap.vo.DepartmentVo1;
import com.pepper.model.emap.vo.EventDispatchVo;
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
import com.pepper.service.emap.event.EventListAssistService;
import com.pepper.service.emap.event.EventListService;
import com.pepper.service.emap.event.EventMessageService;
import com.pepper.service.emap.event.EventRuleService;
import com.pepper.service.emap.event.HelpListService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.map.MapImageUrlService;
import com.pepper.service.emap.map.MapService;
import com.pepper.service.emap.message.MessageService;
import com.pepper.service.emap.node.NodeService;
import com.pepper.service.emap.node.NodeTypeService;
import com.pepper.service.emap.staff.StaffService;
import com.pepper.service.file.FileService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;
import com.pepper.util.BeanToMapUtil;
import com.pepper.util.HttpUtil;
import com.pepper.util.MapToBeanUtil;

@Controller
@RequestMapping("/front/event")
public class EventListController extends BaseControllerImpl implements BaseController {

	@Reference
	private EventListService eventListService;
	
	@Reference
	private EventListAssistService eventListAssistService;
	
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
	
	@Reference
	private StaffService staffService;
	
	@Reference
	private EventMessageService eventMessageService;

	@Reference
	private RoleService roleService;

	@RequestMapping(value = "/add")
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		EventList eventList = new EventList();
		MapToBeanUtil.convert(eventList, map);
		eventList.setIsOperatorTransfer(false);		
//		if(map.containsKey("isConsole")&&map.get("isConsole")!=null&&((Boolean)map.get("isConsole"))) {
//			AdminUser currentUser = (AdminUser) this.getCurrentUser();
//			eventList.setStatus("W");
//			eventList.setOperator(currentUser.getId());
//		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			dateFormat.parse(eventList.getEventDate());
		}catch (Exception e) {
			eventList.setEventDate(null);
		}
		
		if(!StringUtils.hasText(eventList.getEventDate())) {
			eventList.setCreateDate(new Date());
			eventList.setEventDate(dateFormat.format(eventList.getCreateDate()));
		}
		eventList = eventListService.save(eventList);
		return resultData;
	}
	
	@RequestMapping("/workbench/list")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object list(Boolean isUrgent,String id) {
		Pager<EventList> pager = new Pager<EventList>();
		pager.getJpqlParameter().setSortParameter("warningLevel", Direction.DESC);
		pager.getJpqlParameter().setSortParameter("createDate", Direction.DESC);
		String userId = ((AdminUser)this.getCurrentUser()).getId();
		List<String> sourceCode= eventListService.userNode(userId);
		pager.getJpqlParameter().getSearchParameter().put(SearchConstant.IN+"_sourceCode", sourceCode);
		pager = eventListService.List(pager,isUrgent);
		List<EventList> list = pager.getResults();
		pager.setResults(null);
		pager.setData("eventList",convertVo(list));
//		systemLogService.log("get event list", this.request.getRequestURL().toString());
		return pager;
	}
	
	@RequestMapping("/workbench/automatic/list")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object automaticList(String id) {
		Pager<EventList> pager = new Pager<EventList>();
		pager.getJpqlParameter().setSearchParameter(SearchConstant.IN+"_status", new String[] {"A","B"});
		pager.getJpqlParameter().setSearchParameter(SearchConstant.IS_NULL+"_operator", null);
		if(StringUtils.hasText(id)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_id", id);
		}
		String userId = ((AdminUser)this.getCurrentUser()).getId();
		List<String> sourceCode= eventListService.userNode(userId);
		pager.getJpqlParameter().setSearchParameter(SearchConstant.IN+"_sourceCode", sourceCode);
		pager.getJpqlParameter().setSortParameter("warningLevel", Direction.DESC);
		pager.getJpqlParameter().setSortParameter("createDate", Direction.DESC);
		pager = eventListService.findNavigator(pager);
		List<EventList> list = pager.getResults();
		pager.setResults(null);
		pager.setData("eventList",convertVo(list));
//		systemLogService.log("get event automatic list", this.request.getRequestURL().toString());
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
				resultData.setMessage(Internationalization.getMessageInternationalization(9000001).replace("{1}", eventList.getEventName()));
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
//		systemLogService.log("event to me", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping("/workbench/forMe/handle")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object eventForMeHandle(Boolean isHandle,String id,@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate,@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate) {
//		systemLogService.log("event for me handle", this.request.getRequestURL().toString());
		return eventForMeEx(true,id,startDate,endDate);
	}
	
	@RequestMapping("/workbench/forMe/noHandle")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object eventForMeNoHandle(Boolean isHandle,String id,@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate,@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate) {
//		systemLogService.log("event for me no handle", this.request.getRequestURL().toString());
		return eventForMeEx(false,id,startDate,endDate);
	}
	
	private Pager<EventList> eventForMeEx(Boolean isHandle,String id, Date startDate, Date endDate){
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
		if(startDate!=null) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.GREATER_THAN_OR_EQUAL_TO+"_createDate", startDate);
		}
		if(endDate!=null) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LESS_THAN_OR_EQUAL_TO+"_createDate", endDate);
		}
		pager.getJpqlParameter().setSortParameter("warningLevel", Direction.DESC);
		pager.getJpqlParameter().setSortParameter("createDate", Direction.DESC);
		List<Node> listNode =  this.nodeService.findAll();
		List<String> sourceCode = new ArrayList<>();
		listNode.forEach(node ->{
			sourceCode.add(node.getSourceCode());
		});
//		String userId = ((AdminUser)this.getCurrentUser()).getId();
//		List<String> sourceCode= eventListService.userNode(userId);
		pager.getJpqlParameter().getSearchParameter().put(SearchConstant.IN+"_sourceCode", sourceCode);
		pager = eventListService.findNavigator(pager);
		List<EventList> list = pager.getResults();
		pager.setResults(null);
		List<EventListVo> listEventListVo = convertVo(list);
		setEventMessage(listEventListVo);
		pager.setData("eventList",listEventListVo);
		
		return pager;
	}
	
	@RequestMapping("/workbench/eventHelp")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object eventHelp(String id,String eventId) {
		
		Pager<HelpList> pager = new Pager<HelpList>();
		Node node = nodeService.findById(id);
		EventList eventList = this.eventListService.findById(eventId);
		int warningLevel = 0;
		if(eventList!=null) {
			warningLevel=eventList.getWarningLevel();
		}
		if(node!=null && StringUtils.hasText(node.getNodeTypeId())) {
			
			pager = helpListService.findByNodeTypeIdAndWarningLevelLessThanEqual(node.getNodeTypeId(),warningLevel,pager);
			pager.setData("helpList", pager.getResults());
			pager.setResults(null);
		}
//		systemLogService.log("get event help", this.request.getRequestURL().toString());
		return pager;
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
				departmentGroupV1.setUser(listAdminUserVo);
				listDepartmentGroupV1.add(departmentGroupV1);
			}
			listDepartmentGroupV1.add(setDepartmentManager(department.getId()));
			listDepartmentGroupV1.add(setNoDepartment());
			departmentVo1.setDepartmentGroup(listDepartmentGroupV1);
			returnList.add(departmentVo1);
		}
		resultData.setData("department", returnList);
		return resultData;
	}
	
	private DepartmentGroupVo1 setDepartmentManager(String departmentId) {
		List<AdminUser> listAdminUser  = adminUserService.findByDepartmentId(departmentId, true);
//		listAdminUser.addAll(adminUserService.findEmployeeUserNotInDepartment());
		DepartmentGroupVo1 departmentGroupV1 = new DepartmentGroupVo1();
		departmentGroupV1.setName("未分組");
		List<AdminUserVo> listAdminUserVo = new ArrayList<AdminUserVo>();
		for(AdminUser adminUser : listAdminUser) {
			AdminUserVo adminUserVo = new AdminUserVo();
			BeanUtils.copyProperties(adminUser, adminUserVo);
			adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(adminUser.getHeadPortrait()));
			listAdminUserVo.add(adminUserVo);
		}
		departmentGroupV1.setUser(listAdminUserVo);
		return departmentGroupV1;
	}

	private DepartmentGroupVo1 setNoDepartment() {
		List<AdminUser> listAdminUser  = adminUserService.findEmployeeUserNotInDepartment();
		DepartmentGroupVo1 departmentGroupV1 = new DepartmentGroupVo1();
		departmentGroupV1.setName("无部门");
		List<AdminUserVo> listAdminUserVo = new ArrayList<AdminUserVo>();
		for(AdminUser adminUser : listAdminUser) {
			AdminUserVo adminUserVo = new AdminUserVo();
			BeanUtils.copyProperties(adminUser, adminUserVo);
			adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(adminUser.getHeadPortrait()));
			listAdminUserVo.add(adminUserVo);
		}
		departmentGroupV1.setUser(listAdminUserVo);
		return departmentGroupV1;
	}


	
//	
	
	@RequestMapping("/workbench/retransmission")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object retransmission(@RequestBody String str) throws JsonParseException, JsonMappingException, IOException {
		
		systemLogService.log("event  retransmission", this.request.getRequestURL().toString());
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String,Object> map = objectMapper.readValue(str, Map.class);
		EventList eventList = this.eventListService.findById(map.get("eventId").toString());
		sendEmail(eventList);
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
		AdminUser user =  (AdminUser) this.getCurrentUser();
		Map<String,Object> map = objectMapper.readValue(str, Map.class);
//		String eventId,String employeeId,String helpId,String content 
		ResultData resultData = new ResultData();
		EventList eventList = this.eventListService.findById(map.get("eventId").toString());
		if(eventList==null) {
			resultData.setCode(9000002);
			resultData.setMessage(Internationalization.getMessageInternationalization(9000002));
			return resultData;
		}
		
		if(!eventList.getOperator().equals(user.getId())) {
			resultData.setCode(9000007);
			resultData.setMessage(Internationalization.getMessageInternationalization(9000007));
			return resultData;
		}
		
		this.eventListAssistService.delete(eventList.getId(), eventList.getCurrentHandleUser(), null);
		if(!map.containsKey("employeeId")) {
			resultData.setCode(900003);
			resultData.setMessage(Internationalization.getMessageInternationalization(9000003));
			return resultData;
		}
		eventList.setIsUnableFinish(false);
		eventList.setIsNotFoundEmployee(false);
		eventList.setIsNotFoundEventRule(false);
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
		eventDispatch.setDepartmentId(adminUserService.findById(eventList.getCurrentHandleUser()).getDepartmentId());
		eventDispatch.setEventId(eventList.getEventId());
		eventDispatch.setEventListId(eventList.getId());
		eventDispatch.setContent(map.get("content")==null?"":map.get("content").toString());
		eventDispatch.setOperator(map.get("employeeId").toString());
		eventDispatch.setDispatchFrom(adminuser.getId());
		eventDispatch.setTitle(eventList.getEventName());
		eventDispatch.setAssignDate(new Date());
		eventDispatchService.save(eventDispatch);
		
		try {
			String employeeId = map.get("employeeId").toString();
			String deviceId = valueOperationsService.get("userDeviceId_"+employeeId);
			messageService.send(deviceId, Internationalization.getMessageInternationalization(7000001),eventList.getEventName(),eventList.getId());
		}catch (Exception e) {
			// TODO: handle exception
		}
		return resultData;
	}
	
	@RequestMapping("/workbench/historyEvent")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object historyEvent(String id,String nodeId) throws IOException {
		Pager<EventList> pager = new Pager<EventList>();
		
		
		if(StringUtils.hasText(id)) {
			EventList eventList = this.eventListService.findById(id);
			if(eventList==null) {
				return pager;
			}
			pager = this.eventListService.findBySourceCodeAndIdNot(eventList.getSourceCode(), eventList.getId(),pager);
		}else if(StringUtils.hasText(nodeId)) {
			Node node = this.nodeService.findById(nodeId);
			if(node==null) {
				return pager;
			}
			pager = eventListService.findBySourceCodeAndIdNot( node.getSourceCode(), "1111111111", pager);
		}else {
			return pager;
		}
		
		
		List<EventList> list= pager.getResults();
		
		
		pager.setData("historyEvent", convertHistoryEventList(list));
		pager.setResults(null);
//		systemLogService.log("event history list", this.request.getRequestURL().toString());
		return pager;
	}
	
	@RequestMapping("/workbench/historyEventList")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object historyEventList(String event,Integer warningLevel,String node,String nodeTypeId,String mapName,String buildName,String siteName,String operatorId,String status,String eventId,@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")Date startDate,@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")Date endDate
		, String departmentId){
		Pager<EventList> pager = new Pager<EventList>();
		pager = this.eventListService.historyEventList(pager, event, warningLevel, node, nodeTypeId, mapName, buildName, siteName, operatorId, status, eventId, startDate, endDate,departmentId);
		
		pager.setData("historyEvent",  convertHistoryEventList(pager.getResults()));
		pager.setResults(null);
//		systemLogService.log("event historyEventList ", this.request.getRequestURL().toString());
		return pager;
	}
	
	
	private List<EventListVo> convertHistoryEventList(List<EventList> list){
		List<EventListVo> returnList  = new ArrayList<EventListVo>();
		for(EventList obj : list) {
			EventListVo eventListVo = new EventListVo();
			BeanUtils.copyProperties(obj, eventListVo);
			setNode(obj,eventListVo);
			if(StringUtils.hasText(obj.getOperator())) {
				AdminUser operatorUser = this.adminUserService.findById(obj.getOperator());
				if(operatorUser!=null) {
					AdminUserVo adminUserVo = new AdminUserVo();
					BeanUtils.copyProperties(operatorUser, adminUserVo);
					eventListVo.setVideoUrl(this.fileService.getUrl(obj.getVideo()));
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
		setEventMessage(returnList);
		return returnList;
	}
	
	private void setEventMessage(List<EventListVo> list) {
		for(EventListVo eventListVo : list) {
			List<EventMessage> listEventMessage= this.eventMessageService.findEventMessage(eventListVo.getId());
			eventListVo.setEventMessage(listEventMessage);
		}
	}
	
	@RequestMapping("/workbench/eventDispatch")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object eventDispatch(String eventId) throws IOException {
		ResultData resultData = new ResultData();
		List<EventDispatch> list = this.eventDispatchService.findEventDispatch(eventId);
		List<EventDispatchVo> returnList = new ArrayList<EventDispatchVo>();
		for(EventDispatch eventDispatch : list)  {
			EventDispatchVo eventDispatchVo = new EventDispatchVo();
			BeanUtils.copyProperties(eventDispatch, eventDispatchVo);
			eventDispatchVo.setImageUrl1(this.fileService.getUrl(eventDispatch.getImage1()));
			eventDispatchVo.setImageUrl2(this.fileService.getUrl(eventDispatch.getImage2()));
			eventDispatchVo.setImageUrl3(this.fileService.getUrl(eventDispatch.getImage3()));
			eventDispatchVo.setVoiceUrl1(this.fileService.getUrl(eventDispatch.getVoice1()));
			eventDispatchVo.setVideoUrl(this.fileService.getUrl(eventDispatch.getVideo()));
			if(StringUtils.hasText(eventDispatch.getOperator())) {
				AdminUser operatorUser = this.adminUserService.findById(eventDispatch.getOperator());
				if(operatorUser!=null) {
					eventDispatchVo.setOperatorName(operatorUser.getName());
					eventDispatchVo.setOperatorHeadPortraitUrl(this.fileService.getUrl(operatorUser.getHeadPortrait()));
				}
			}
			if(StringUtils.hasText(eventDispatch.getDispatchFrom())) {
				AdminUser dispatchFromUser = this.adminUserService.findById(eventDispatch.getDispatchFrom());
				if(dispatchFromUser!=null) {
					eventDispatchVo.setDispatchFromName(dispatchFromUser.getName());
					eventDispatchVo.setDispatchFromHeadPortraitUrl(this.fileService.getUrl(dispatchFromUser.getHeadPortrait()));
				}
			}
			returnList.add(eventDispatchVo);
		}
		resultData.setData("eventDispatch", returnList);
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
			tmp.setVideoUrl(this.fileService.getUrl(tmp.getVideo()));
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
			
			List<EventListAssist> listEventListAssist= this.eventListAssistService.findByActionListId(obj1.getId());
			List<Map<String,Object>> assist = new ArrayList<Map<String,Object>>();
			for(EventListAssist eventListAssist : listEventListAssist) {
				Map<String,Object> map = BeanToMapUtil.transBeanToMap(eventListAssist);
				AdminUser user =  this.adminUserService.findById(eventListAssist.getUserId());
				map.put("assistUserName", user==null?"":user.getName());
				map.put("isFinish",eventListAssist.getIsFinish());
				assist.add(map);
			}
			
			tmp.setAssist(assist);
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/workbench/filed")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object filed(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		if(map.containsKey("helpList")) {
			Map<String,Object> helpList = (Map<String, Object>) map.get("helpList");
			if(helpList.containsKey("update")) {
				List<Map<String, Object>> updateList =(List<Map<String, Object>>) helpList.get("update");
				for(Map<String, Object> updateMap : updateList ) {
					if(updateMap.containsKey("id")) {
						deleteHelpList(updateMap.get("id").toString());
						updateHelpList(updateMap);
					}
				}
			}
			if(helpList.containsKey("delete")) {
				List<String> deleteList = (List<String>) helpList.get("delete");
				for(String id : deleteList) {
					deleteHelpList(id);
				}
			}
			if(helpList.containsKey("add")) {
				List<Map<String, Object>> addList =(List<Map<String, Object>>) helpList.get("add");
				String nodeTypeId = null;
				if(map.containsKey("id")) {
					EventList eventList = this.eventListService.findById(map.get("id").toString());
					nodeTypeId = this.nodeService.findBySourceCode(eventList.getSourceCode()).getNodeTypeId();
				}
				for(Map<String, Object> addMap : addList ) {
					addMap.put("nodeTypeId", nodeTypeId);
					addHelpList(addMap);
				}
			}	
		}
		
		
		if(map.containsKey("id")) {
			EventList eventList = this.eventListService.findById(map.get("id").toString());
			eventList.setStatus("P");
			if(map.containsKey("video")) {
				eventList.setVideo(Objects.isNull(map.get("video"))?"":map.get("video").toString());
			}
			if(map.containsKey("filedUserNo")){
				String filedUserNo = String.valueOf(map.get("filedUserNo"));
//				if(!StringUtils.hasText(filedUserNo)){
//					resultData.setCode(Status.FAIL.getKey());
//					resultData.setMessage("工号不存在！");
//					return resultData;
//				}
//				AdminUser user = this.adminUserService.findByUserNo(filedUserNo);
//				if(Objects.isNull(user)){
//					resultData.setCode(Status.FAIL.getKey());
//					resultData.setMessage("工号不存在！");
//					return resultData;
//				}
				eventList.setFiledUserNo(filedUserNo);
			}
			eventList.setFiledContent(map.containsKey("filedContent")?Objects.isNull(map.get("filedContent"))?"":map.get("filedContent").toString():"");
			eventListService.update(eventList);
			sendEmail(eventList);
		}
		systemLogService.log("event filed", this.request.getRequestURL().toString());
		
		return resultData;
	}
	
	@RequestMapping("/workbench/eventListHelpList")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object eventListHelpList(String id) throws IOException {
		ResultData resultData = new ResultData();
		EventList eventList = this.eventListService.findById(id);
		List<ActionList> list = this.actionListService.findActionList(eventList.getId());
		ObjectMapper objectMapper = new ObjectMapper();
		List<HelpList> returnList = new ArrayList<HelpList>();
		if(Objects.nonNull(list)&&list.size()>0) {
			ActionList actionList = list.get(0);
			String helpList = actionList.getHelpId();
			if(StringUtils.hasText(helpList)) {
				ArrayNode arrayNode =  (ArrayNode) objectMapper.readTree(helpList);
				for(JsonNode jsonNode : arrayNode) {
					returnList.add(this.helpListService.findById(jsonNode.asText()));
				}
			}
		}
		resultData.setData("helpList", returnList);
		return resultData;
	}
	
	private void addHelpList(Map<String, Object> map) {
		HelpList helpList = new HelpList();
		MapToBeanUtil.convert(helpList, map);
		
//		helpList.setNodeTypeId(nodeTypeId);
		helpListService.save(helpList);
	}
	
	private void updateHelpList(Map<String, Object> map) {
		HelpList helpList = new HelpList();
		MapToBeanUtil.convert(helpList, map);
		helpListService.update(helpList);
	}
	private void deleteHelpList(String id) {
		HelpList helpList = this.helpListService.findById(id);
		if(helpList!=null) {
			helpList.setIsDelete(true);
			this.helpListService.update(helpList);
		}
	}
	
	@RequestMapping("/workbench/toOperator")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object toOprrator(@RequestBody Map<String,String> map) {
		ResultData resultData = new ResultData();
		String operatorId = map.get("operatorId").toString();
		AdminUser adminUser = this.adminUserService.findById(operatorId);
		if(adminUser==null || !adminUser.getIsWork()) {
			resultData.setCode(9000004);
			resultData.setMessage(Internationalization.getMessageInternationalization(9000004));
			return resultData;
		}
		String eventId =  map.get("eventId").toString();
		EventList eventList = this.eventListService.findById(eventId);
		if(eventList == null) {
			resultData.setCode(9000002);
			resultData.setMessage(Internationalization.getMessageInternationalization(9000002));
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
//		systemLogService.log("event to operator round robin", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping("/workbench/door/attendance")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object attendance(String eventListId,String nodeId,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startDate,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endDate,String staffId) {
		Pager<EventList> pager = new Pager<EventList>();
		pager = eventListService.doorAttendance(pager, eventListId,nodeId, startDate, endDate, staffId);
		pager.setData("eventList", convertVo(pager.getResults()));
		pager.setResults(null);
		return pager;
		
	}
	
	@RequestMapping("/workbench/forNode")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object forNode(String nodeId) {
		Pager<EventList> pager = new Pager<EventList>();
		Node node =  this.nodeService.findById(nodeId);
		pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+ "_sourceCode", node==null?null:node.getSourceCode());
		pager.getJpqlParameter().setSearchParameter(SearchConstant.NOT_EQUAL+"_status","P");
		pager.getJpqlParameter().setSortParameter("createDate", Direction.DESC);
		pager = this.eventListService.findNavigator(pager);
		List<EventListVo> list = this.convertVo(pager.getResults());
		pager.setData("eventList", list);
		pager.setResults(null);
		return pager;
	}
	
	@RequestMapping("/workbench/sms")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object sms(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		EventMessage eventMessage = new EventMessage();
		MapToBeanUtil.convert(eventMessage, map);
		EventList eventList = this.eventListService.findById(eventMessage.getEventListId());
		if(eventList==null) {
			resultData.setCode(9000002);
			resultData.setMessage(Internationalization.getMessageInternationalization(9000002));
			return resultData;
		}
		
		AdminUser user = this.adminUserService.findById(eventList.getCurrentHandleUser());
		if(user==null) {
			resultData.setCode(9000005);
			resultData.setMessage(Internationalization.getMessageInternationalization(9000005));
			return resultData;
		}
		
		if(!StringUtils.hasText(user.getMobile()))
		{
			resultData.setCode(9000005);
			resultData.setMessage(Internationalization.getMessageInternationalization(9000005));
			return resultData;
		}
		eventMessage.setMobile(user.getMobile());
		eventMessage.setUserId(user.getId());
		eventMessage.setUserName(user.getName());
		eventMessage.setType(2);
		eventMessageService.save(eventMessage);
		return resultData;
	}
	
	@RequestMapping("/workbench/cout")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object count() {
		ResultData resultData = new ResultData();
		resultData.setData("count", actionListService.count());
		return resultData;
	}

	@RequestMapping("/workbench/automatic/filed")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object automaticFiled(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Boolean automaticFiled = map.containsKey("automaticFiled")?Boolean.valueOf(String.valueOf(map.get("automaticFiled"))) :false;
		String account = String.valueOf(map.get("account"));
		String password = String.valueOf(map.get("password"));
		AdminUser user = adminUserService.findByAccountAndPassword(account, password);
		if(Objects.isNull(user)){
			resultData.setCode(Status.FAIL.getKey());
			resultData.setMessage("该账号不存在");
			return resultData;
		}
		List<Role> list = roleService.findByUserId1(user.getId());
		Boolean b = false;
		for (Role role : list){
			if(role.getCode().equals("ADMIN_ROLE")){
				b = true;
			}
		}
		if(!b){
			resultData.setCode(Status.FAIL.getKey());
			resultData.setMessage("该账号角色非ADMIN_ROLE！");
			return resultData;
		}
		valueOperationsService.set("automaticFiled",String.valueOf(automaticFiled));
		return resultData;
	}

	@RequestMapping("/workbench/automatic/filed/info")
	@ResponseBody
	@Authorize(authorizeResources = false)
	public Object automaticFiledInfo() {
		ResultData resultData = new ResultData();
		Boolean automaticFiled = Boolean.valueOf(valueOperationsService.get("automaticFiled"));
		resultData.setData("automaticFiled",automaticFiled);
		return resultData;
	}

	
		
	private List<EventListVo> convertVo(List<EventList> list){
		List<EventListVo> returnList  = new ArrayList<EventListVo>();
		for(EventList eventList : list) {
			EventListVo eventListVo = new EventListVo();
			BeanUtils.copyProperties(eventList, eventListVo);
			setNode(eventList,eventListVo);
			if(StringUtils.hasText(eventList.getCurrentHandleUser()) ) {
				AdminUser currentHandleUser = this.adminUserService.findById(eventList.getCurrentHandleUser());
				if(currentHandleUser!=null) {
					AdminUserVo adminUserVo = new AdminUserVo();
					BeanUtils.copyProperties(currentHandleUser, adminUserVo);
					eventListVo.setVideoUrl(this.fileService.getUrl(eventList.getVideo()));
					adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(currentHandleUser.getHeadPortrait()));
					eventListVo.setCurrentHandleUserVo(adminUserVo);
				}
			}
			if(StringUtils.hasText(eventList.getIdCard())) {
				List<Staff> listStaff = staffService.findByIdCard(eventList.getIdCard());
				eventListVo.setStaff(listStaff.size()>0?listStaff.get(0):null);
			}
			
//			List<EventListAssist> listEventListAssist = this.eventListAssistService.findEventListAssist(eventList.getId());
//			List<Map<String,Object>> listEventListAssistMap= new ArrayList<Map<String,Object>>();
//			for(EventListAssist eventListAssist :  listEventListAssist) {
//				Map<String,Object> map = new HashMap<String, Object>();
//				AdminUser user =  this.adminUserService.findById(eventListAssist.getUserId());
//				map.put("assistUserName", user==null?"":user.getName());
//				map.put("finish", eventListAssist.getIsEmployeeConfirmFinish());
//				listEventListAssistMap.add(map);
//			}
//			eventListVo.setAssist(listEventListAssistMap);
			returnList.add(eventListVo);
		}
		return returnList;
	}
	
	private void setNode(EventList eventList,EventListVo eventListVo) {
		Node node = nodeService.findBySourceCode(StringUtils.hasText(eventList.getSourceCode())?eventList.getSourceCode():"111111111");
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
					nodeTypeVo.setProcessingIconUrl(fileService.getUrl(nodeType.getProcessingIcon()));
					nodeTypeVo.setStopIconUrl(fileService.getUrl(nodeType.getStopIcon()));
					nodeVo.setNodeType(nodeTypeVo);
				}
				mapVo.setMapImageUrl(mapImageUrlService.findByMapId(map.getId()));
			}
			eventListVo.setNode(nodeVo);
		}
	}
	
	private void sendEmail(EventList eventList) {
		if(Objects.isNull(eventList.getCurrentHandleUser())) {
			return ;
		}
		AdminUser admiUser = this.adminUserService.findById(eventList.getCurrentHandleUser());
		if(Objects.isNull(admiUser)) {
			return ;
		}
		List<AdminUser> listManagerUser= adminUserService.findByDepartmentIdAndIsManagerAndDepartmentGroupIdIsNullOrDepartmentGroupId(admiUser.getDepartmentId(),true);
		List<ActionList> list = this.actionListService.findActionList(eventList.getId());
		if(Objects.isNull(list) || list.size()<=0) {
			return ;
		}
		ActionList actionList = list.get(0);
		for(AdminUser adminUser : listManagerUser) {
			if(StringUtils.hasText(adminUser.getEmail())) {
				EventMessage eventMessage = new EventMessage();
				eventMessage.setType(1);
				eventMessage.setTitle(eventList.getEventName()+"處理情況");
				eventMessage.setEmail(adminUser.getEmail());
				eventMessage.setMessage(eventList.getEventName()+"處理情況："+(actionList==null?"":actionList.getContent()));
				this.eventMessageService.save(eventMessage);
			}
		}
	}
	
}
