package com.pepper.controller.emap.app.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.event.ActionList;
import com.pepper.model.emap.event.EventDispatch;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventListAssist;
import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.event.HelpList;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.vo.ActionListVo;
import com.pepper.model.emap.vo.AdminUserVo;
import com.pepper.model.emap.vo.EventDispatchVo;
import com.pepper.model.emap.vo.EventListAssistVo;
import com.pepper.model.emap.vo.EventListVo;
import com.pepper.model.emap.vo.EventListVo1;
import com.pepper.model.emap.vo.HelpListVo;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.model.emap.vo.NodeTypeVo;
import com.pepper.model.emap.vo.NodeVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.emap.event.ActionListService;
import com.pepper.service.emap.event.EventDispatchService;
import com.pepper.service.emap.event.EventListAssistService;
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
import com.pepper.util.BeanToMapUtil;

import it.sauronsoftware.jave.AudioUtils;

@Controller ( value = "appEventListController" )
@RequestMapping ( "/app/event" )
public class EventListController extends BaseControllerImpl implements BaseController {

    @Reference
    private EventListService eventListService;

    @Reference
    private EventListAssistService eventListAssistService;

    @Reference
    private EventRuleService eventRuleService;

    @Resource
    private Environment environment;

    @Resource
    private AdminUserService adminUserService;

    @Resource
    private FileService fileService;

    @Reference
    private EventDispatchService eventDispatchService;

    @Reference
    private NodeService nodeService;

    @Reference
    private HelpListService helpListService;

    @Reference
    private ActionListService actionListService;

    @Reference
    private MapService mapService;

    @Reference
    private NodeTypeService nodeTypeService;

    @Reference
    private MapImageUrlService mapImageUrlService;

    @Reference
    private ValueOperationsService valueOperationsService;

    @Reference
    private MessageService messageService;

    @Reference
    private SystemLogService systemLogService;

    @RequestMapping("/list")
    @ResponseBody
    @Authorize(authorizeResources = false)
    public Object list(@RequestParam(value = "isFinish", defaultValue = "true") Boolean isFinish, String eventId, Boolean isUrgent, String nodeName, String eventName, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate) throws IOException {
        AdminUser adminUser = (AdminUser) this.getCurrentUser();
        Pager<EventList> pager = new Pager<EventList>();
        pager = eventListService.appList(pager, isFinish, isUrgent, eventId, nodeName, eventName, startDate, endDate, adminUser.getId());
        pager.setData("eventList", convertVo(pager.getResults()));
        pager.setResults(null);
        return pager;
    }

    @RequestMapping("/finish/list")
    @ResponseBody
    @Authorize(authorizeResources = false)
    public Object finishList() throws IOException {
        AdminUser adminUser = (AdminUser) this.getCurrentUser();
        Pager<ActionList> pager1 = new Pager<ActionList>();
        pager1.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL + "_status", "B");
        pager1.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL + "_operator", adminUser.getId());
//		pager1.getJpqlParameter().setSearchParameter(SearchConstant.IS_FALSE+ "_isAssist",null);
        pager1.getJpqlParameter().setSortParameter("assignDate", Direction.DESC);
        pager1 = actionListService.findNavigator(pager1);
        List<ActionList> list = pager1.getResults();
        List<EventListVo> listEventList = new ArrayList<EventListVo>();
        for (ActionList actionList : list) {
            EventList eventList = this.eventListService.findById(actionList.getEventListId());
//			ActionListVo actionListVo = new ActionListVo();
//			BeanUtils.copyProperties(actionList, actionListVo);
//			actionListVo.setEventList(this.convertVo(eventList));
            if (eventList != null) {
                EventListVo eventListVo = this.convertVo(eventList);
                eventListVo.setActionListId(actionList.getId());
                listEventList.add(eventListVo);
            }
        }
        pager1.setData("eventList", listEventList);
        pager1.setResults(null);
        return pager1;
    }

    @RequestMapping("/actionList")
    @ResponseBody
    @Authorize(authorizeResources = false)
    public Object actionList(String id) throws IOException {
//		systemLogService.log("App action list", this.request.getRequestURL().toString());
        ResultData resultData = new ResultData();
        ActionList actionList = actionListService.findById(id);
        EventList eventList = this.eventListService.findById(actionList.getEventListId());
        Node node = nodeService.findBySourceCode(eventList.getSourceCode());
//		ActionListVo actionListVo = new ActionListVo();
//		BeanUtils.copyProperties(actionList, actionListVo);
//		actionListVo.setImageUrl1(this.fileService.getUrl(actionList.getImage1()));
//		actionListVo.setImageUrl2(this.fileService.getUrl(actionList.getImage2()));
//		actionListVo.setImageUrl3(this.fileService.getUrl(actionList.getImage3()));
//		actionListVo.setVoiceUrl1(this.fileService.getUrl(actionList.getVoice1()));
//		EventList eventList = this.eventListService.findById(actionList.getEventListId());
//		
//		if(node!=null && StringUtils.hasText(node.getNodeTypeId())) {
//			actionListVo.setHelpList(convertHelpList(helpListService.findByNodeTypeId(node.getNodeTypeId()),actionList.getHelpId(),actionList.getOperatorHelpId()));
//		}
//		if(StringUtils.hasText(actionList.getOperator())) {
//			AdminUser operatorUser = this.adminUserService.findById(actionList.getOperator());
//			if(operatorUser!=null) {
//				AdminUserVo adminUserVo = new AdminUserVo();
//				BeanUtils.copyProperties(operatorUser, adminUserVo);
//				adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(operatorUser.getHeadPortrait()));
//				adminUserVo.setPassword("");
//				actionListVo.setEmployee(adminUserVo);
//			}
//		}
        resultData.setData("actionList", this.convertActionListVo(actionList, node));
        return resultData;
    }

    @RequestMapping("/transferList")
    @ResponseBody
    @Authorize(authorizeResources = false)
    public Object transferList(String eventId, Boolean isUrgent, String nodeName, String eventName, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate) throws IOException {
        Pager<EventList> pager = new Pager<EventList>();
        AdminUser adminUser = (AdminUser) this.getCurrentUser();
        pager = eventListService.transferList(pager, adminUser.getId(), eventId, isUrgent, nodeName, eventName, startDate, endDate);
        pager.setData("eventList", convertVo(pager.getResults()));
        pager.setResults(null);

//		systemLogService.log("App event transfer list", this.request.getRequestURL().toString());
        return pager;

    }

    @RequestMapping("/info")
    @ResponseBody
    @Authorize(authorizeResources = false)
    public Object info(String id, String actionId) throws IOException {
        ResultData resultData = new ResultData();
        EventList eventList = this.eventListService.findById(id);
        if (eventList == null) {
            return resultData;
        }
        resultData.setData("source", eventList.getSource());
        resultData.setData("sourceCode", eventList.getSourceCode());
        resultData.setData("id", eventList.getId());
        resultData.setData("eventId", eventList.getEventId());
        resultData.setData("eventName", eventList.getEventName());
        resultData.setData("createDate", eventList.getCreateDate());
        resultData.setData("eventDate", eventList.getEventDate());
        resultData.setData("content", eventList.getContent());
        AdminUser adminUser = (AdminUser) this.getCurrentUser();
        EventDispatch eventDispatch = this.eventDispatchService.findEventDispatch(eventList.getId(), adminUser.getId());
        if (eventDispatch != null) {
            AdminUser dispatchFrom = this.adminUserService.findById(eventDispatch.getDispatchFrom());
            if (dispatchFrom == null) {
                resultData.setData("dispatchFromName", Internationalization.getMessageInternationalization(7000002));
            } else {
                resultData.setData("dispatchFromName", dispatchFrom.getName());
            }
            resultData.setData("dispatchDate", eventDispatch.getCreateDate());

        } else {
            resultData.setData("dispatchFromName", "");
            resultData.setData("dispatchDate", null);
        }
        resultData.setData("warningLevel", eventList.getWarningLevel());
        resultData.setData("status", eventList.getStatus());

        Node node = nodeService.findBySourceCode(eventList.getSourceCode());
        if (node != null) {
            resultData.setData("nodeName", node.getName());
            resultData.setData("nodeId", node.getId());
            resultData.setData("nodeCode", node.getCode());
            resultData.setData("helpList", convertHelpList(helpListService.findByNodeTypeId(node.getNodeTypeId()), null, eventList.getHelpId()));
            NodeType nodeType = this.nodeTypeService.findById(node.getNodeTypeId());
            if (Objects.nonNull(nodeType)) {
                resultData.setData("nodeTypeCode", nodeType.getCode());
                resultData.setData("nodeTypeName", nodeType.getName());
            } else {
                resultData.setData("nodeTypeCode", "");
                resultData.setData("nodeTypeName", "");
            }

        }


        if (StringUtils.hasText(actionId)) {
            ActionList actionList = actionListService.findById(actionId);
            List<ActionListVo> listActionListVo = new ArrayList<ActionListVo>();
            listActionListVo.add(convertActionListVo(actionList, node));
            resultData.setData("actionList", listActionListVo);
        } else {
            resultData.setData("actionList", convertActionListVo(actionListService.findActionList(eventList.getId()), node));
        }
        resultData.setData("eventDispatch", eventDispatch(eventList.getId()));

        resultData.setData("remark", eventList.getContent());
        resultData.setData("currentHelpId", eventList.getHelpId());
        resultData.setData("isUrgent", eventList.getWarningLevel() >= getUrgentWarningLevel(eventList));
        AdminUser user = (AdminUser) this.getCurrentUser();
        if (StringUtils.hasText(eventList.getCurrentHandleUser())) {
            if (eventList.getCurrentHandleUser().equals(user.getId())) {
                resultData.setData("isMeHandle", true);
            } else {
                resultData.setData("isMeHandle", false);
            }
        } else {
            resultData.setData("isMeHandle", false);
        }

        List<Map<String, Object>> assistMap = new ArrayList<Map<String, Object>>();
        if (StringUtils.hasText(actionId)) {
            List<EventListAssist> listEventListAssist = this.eventListAssistService.findByActionListId(actionId);
            assistMap = assistList(listEventListAssist);
        } else {
            List<EventListAssist> listEventListAssist = this.eventListAssistService.findByEventListIdAndEmployeeId(id, eventList.getCurrentHandleUser());
            assistMap = assistList(listEventListAssist);
        }
        resultData.setData("assistList", assistMap);

        EventListAssist eventListAssist = this.eventListAssistService.findEventListAssist(id, adminUser.getId(), eventList.getCurrentHandleUser());
        if (eventListAssist != null) {
            AdminUser requestAssistUser = this.adminUserService.findById(eventListAssist.getEmployeeId());
            resultData.setData("requestAssistName", requestAssistUser.getName());
        } else {
            resultData.setData("requestAssistName", null);
        }


        List<ActionList> listActionList = this.actionListService.findByEventListId(eventList.getId());
        List<ActionListVo> finishActionList = new ArrayList<ActionListVo>();
        for (ActionList actionList : listActionList) {
            finishActionList.add(convertActionListVo(actionList, node));
        }
        resultData.setData("finishActionList", finishActionList);

//		systemLogService.log("App event info", this.request.getRequestURL().toString());
        return resultData;
    }

    public List<EventDispatchVo> eventDispatch(String eventId) throws IOException {
        List<EventDispatch> list = this.eventDispatchService.findEventDispatch(eventId);
        List<EventDispatchVo> returnList = new ArrayList<EventDispatchVo>();
        for (EventDispatch eventDispatch : list) {
            EventDispatchVo eventDispatchVo = new EventDispatchVo();
            BeanUtils.copyProperties(eventDispatch, eventDispatchVo);
            eventDispatchVo.setImageUrl1(this.fileService.getUrl(eventDispatch.getImage1()));
            eventDispatchVo.setImageUrl2(this.fileService.getUrl(eventDispatch.getImage2()));
            eventDispatchVo.setImageUrl3(this.fileService.getUrl(eventDispatch.getImage3()));
            eventDispatchVo.setVoiceUrl1(this.fileService.getUrl(eventDispatch.getVoice1()));
            eventDispatchVo.setVideoUrl(this.fileService.getUrl(eventDispatch.getVideo()));
            if (StringUtils.hasText(eventDispatch.getOperator())) {
                AdminUser operatorUser = this.adminUserService.findById(eventDispatch.getOperator());
                if (operatorUser != null) {
                    eventDispatchVo.setOperatorName(operatorUser.getName());
                    eventDispatchVo.setOperatorHeadPortraitUrl(this.fileService.getUrl(operatorUser.getHeadPortrait()));
                }
            }
            if (StringUtils.hasText(eventDispatch.getDispatchFrom())) {
                AdminUser dispatchFromUser = this.adminUserService.findById(eventDispatch.getDispatchFrom());
                if (dispatchFromUser != null) {
                    eventDispatchVo.setDispatchFromName(dispatchFromUser.getName());
                    eventDispatchVo.setDispatchFromHeadPortraitUrl(this.fileService.getUrl(dispatchFromUser.getHeadPortrait()));
                }
            }
            returnList.add(eventDispatchVo);
        }
        return returnList;
    }

    private List<Map<String, Object>> assistList(List<EventListAssist> list) {
        List<Map<String, Object>> assistMap = new ArrayList<Map<String, Object>>();
        for (EventListAssist eventListAssist : list) {
            Map<String, Object> map = new HashMap<String, Object>();
            map = BeanToMapUtil.transBeanToMap(eventListAssist);
            if (StringUtils.hasText(eventListAssist.getUserId())) {
                AdminUser assistUser = this.adminUserService.findById(eventListAssist.getUserId());
                map.put("userName", assistUser.getName());
                map.put("account", assistUser.getAccount());
                map.put("headPortraitUrl", this.fileService.getUrl(assistUser.getHeadPortrait()));
            }
            assistMap.add(map);
        }
        return assistMap;
    }

    private List<ActionListVo> convertActionListVo(List<ActionList> list, Node node) throws IOException {
        List<ActionListVo> returnList = new ArrayList<>();
        for (ActionList actionList : list) {
            returnList.add(convertActionListVo(actionList, node));
        }
        return returnList;
    }

    private ActionListVo convertActionListVo(ActionList actionList, Node node) throws IOException {
        if (actionList == null) {
            return null;
        }
        ActionListVo actionListVo = new ActionListVo();
        BeanUtils.copyProperties(actionList, actionListVo);
        actionListVo.setImageUrl1(this.fileService.getUrl(actionListVo.getImage1()));
        actionListVo.setImageUrl2(this.fileService.getUrl(actionListVo.getImage2()));
        actionListVo.setImageUrl3(this.fileService.getUrl(actionListVo.getImage3()));
        actionListVo.setVoiceUrl1(this.fileService.getUrl(actionListVo.getVoice1()));
        actionListVo.setVideoUrl(this.fileService.getUrl(actionListVo.getVideo()));
        if (node != null && StringUtils.hasText(node.getNodeTypeId())) {
            actionListVo.setHelpList(convertHelpList(helpListService.findByNodeTypeId(node.getNodeTypeId()), actionList.getHelpId(), actionList.getOperatorHelpId()));

        }
        if (StringUtils.hasText(actionList.getOperator())) {
            AdminUser operatorUser = this.adminUserService.findById(actionList.getOperator());
            if (operatorUser != null) {
                AdminUserVo adminUserVo = new AdminUserVo();
                BeanUtils.copyProperties(operatorUser, adminUserVo);
                adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(operatorUser.getHeadPortrait()));
                adminUserVo.setPassword("");
                actionListVo.setEmployee(adminUserVo);
            }
        }
        return actionListVo;
    }

    private List<HelpListVo> convertHelpList(List<HelpList> helpList, String helpId, String helpId1) throws IOException {
        List<HelpListVo> helpListVo = new ArrayList<HelpListVo>();
        List<String> helpIdList = new ArrayList<String>();
        ObjectMapper objectMapper = new ObjectMapper();
        if (StringUtils.hasText(helpId)) {
            try {
                JsonNode jsonNode = objectMapper.readTree(helpId);
                if (jsonNode.isArray()) {
                    Iterator<JsonNode> array = jsonNode.iterator();
                    while (array.hasNext()) {
                        helpIdList.add(array.next().asText());
                    }
                }
            } catch (Exception e) {

            }
        }

        List<String> listHelpId = new ArrayList<String>();
        if (StringUtils.hasText(helpId1)) {
            JsonNode jsonNode = objectMapper.readTree(helpId1);
            if (jsonNode.isArray()) {
                Iterator<JsonNode> array = jsonNode.iterator();
                while (array.hasNext()) {
                    listHelpId.add(array.next().asText());
                }
            }
        }

        for (HelpList obj : helpList) {
            HelpListVo obj1 = new HelpListVo();
            BeanUtils.copyProperties(obj, obj1);
            obj1.setIsCheck(helpIdList.contains(obj1.getId()));
            if (listHelpId.size() > 0) {
                if (listHelpId.contains(obj1.getId())) {
                    helpListVo.add(obj1);
                }
            } else {
//				helpListVo.add(obj1);
            }
        }
        return helpListVo;
    }

    @RequestMapping("/finish")
    @ResponseBody
    @Authorize(authorizeResources = false)
    public Object finish(@RequestBody String str) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(str);
        String id = jsonNode.get("id").asText("");
        String image1 = jsonNode.get("image1").asText("");
        String image2 = jsonNode.get("image2").asText("");
        String image3 = jsonNode.get("image3").asText("");
        String voice1 = jsonNode.get("voice1").asText("");
        String video = "";
        if (jsonNode.has("video")) {
            video = jsonNode.get("video").asText("");
        }
        Boolean isUnableFinish = jsonNode.get("isUnableFinish").asBoolean(true);
        ResultData resultData = new ResultData();
        EventList eventList = this.eventListService.findById(id);
        if (eventList == null) {
            resultData.setCode(9000002);
            resultData.setMessage(Internationalization.getMessageInternationalization(9000002));
            return resultData;
        }
        eventList.setIsUnableFinish(isUnableFinish);
        eventList.setStatus("B");
        eventListService.update(eventList);

        AdminUser user = (AdminUser) this.getCurrentUser();
        ActionList actionList = new ActionList();
        actionList.setOperator(user.getId());
        actionList.setDepartmentId(user.getDepartmentId());
        actionList.setEventListId(eventList.getId());
        actionList.setEventId(eventList.getEventId());
        actionList.setStatus("B");
        actionList.setOperatorHelpId(eventList.getHelpId());
        actionList.setOperatorContent(eventList.getContent());
        actionList.setImage1(image1);
        actionList.setImage2(image2);
        actionList.setFinishDate(new Date());
        actionList.setAssignDate(eventList.getAssignDate());
        actionList.setImage3(image3);
        actionList.setVoice1(voice1);
        actionList.setVideo(video);
        actionList.setContent(jsonNode.get("content").asText(""));
//		actionList.setIsAssist(false);
        actionList.setIsUnableFinish(isUnableFinish);
        if (jsonNode.has("helpId")) {
            actionList.setHelpId(jsonNode.get("helpId").toString());
        }
        actionList = actionListService.save(actionList);


        List<EventListAssist> eventListAssistList = this.eventListAssistService.findByEventListIdAndEmployeeId(eventList.getId(), user.getId());
        for (EventListAssist eventListAssist : eventListAssistList) {
            eventListAssist.setIsFinish(false);
            eventListAssist.setIsUnableFinish(isUnableFinish);
            eventListAssist.setIsEmployeeConfirmFinish(false);
            eventListAssist.setActionListId(actionList.getId());
            eventListAssistService.update(eventListAssist);
        }

        if (jsonNode.get("assistUserId").isArray()) {
            for (JsonNode assistUserId : jsonNode.get("assistUserId")) {
                if (StringUtils.hasText(assistUserId.asText(""))) {
                    EventListAssist eventListAssist = eventListAssistService.findEventListAssist(eventList.getId(), assistUserId.textValue(), user.getId());
                    if (eventListAssist == null) {
                        continue;
                    }
//					ActionList actionListAssist = new ActionList();
//					actionListAssist.setEventId(eventList.getEventId());
//					actionListAssist.setEventListAssistId(eventListAssist.getId());
//					actionListAssist.setEventListId(eventList.getId());
//					actionListAssist.setOperator(eventListAssist.getUserId());
//					actionListAssist.setAssignDate(eventList.getAssignDate());
//					actionListAssist.setFinishDate(new Date());
//					actionListAssist.setStatus("B");
//					actionListAssist.setOperatorHelpId(eventList.getHelpId());
//					actionListAssist.setOperatorContent(eventList.getContent());
//					actionListAssist.setIsAssist(true);
//					actionListAssist.setIsUnableFinish(isUnableFinish);
//					actionListService.save(actionListAssist);

                    eventListAssist.setIsFinish(true);
                    eventListAssist.setIsEmployeeConfirmFinish(true);
                    eventListAssistService.update(eventListAssist);
                }
            }
        }


        systemLogService.log("App event finish", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping("/toEmployee")
    @ResponseBody
    @Authorize(authorizeResources = false)
    public Object toEmployee(@RequestBody String str) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(str, Map.class);
//		String eventId,String employeeId
        ResultData resultData = new ResultData();
        EventList eventList = this.eventListService.findById(map.get("eventId").toString());
        if (eventList == null) {
            resultData.setCode(8000002);
            resultData.setMessage(Internationalization.getMessageInternationalization(8000002));
            return resultData;
        }
        eventList.setIsUnableFinish(false);
        eventList.setIsNotFoundEmployee(false);
        eventList.setIsNotFoundEventRule(false);
        this.eventListAssistService.delete(eventList.getId(), eventList.getCurrentHandleUser(), null);
        eventList.setAssignDate(new Date());
        eventList.setCurrentHandleUser(map.get("employeeId").toString());
        eventListService.update(eventList);

        AdminUser adminuser = (AdminUser) this.getCurrentUser();
        EventDispatch eventDispatch = new EventDispatch();
        eventDispatch.setDepartmentId(adminUserService.findById(eventList.getCurrentHandleUser()).getDepartmentId());
        eventDispatch.setEventId(eventList.getEventId());
        eventDispatch.setEventListId(eventList.getId());
        eventDispatch.setOperator(map.get("employeeId").toString());
        eventDispatch.setImage1((map.containsKey("image1") && Objects.nonNull(map.get("image1"))) ? map.get("image1").toString() : "");
        eventDispatch.setImage2((map.containsKey("image2") && Objects.nonNull(map.get("image2"))) ? map.get("image2").toString() : "");
        eventDispatch.setImage3((map.containsKey("image3") && Objects.nonNull(map.get("image3"))) ? map.get("image3").toString() : "");
        eventDispatch.setVoice1((map.containsKey("voice1") && Objects.nonNull(map.get("voice1"))) ? map.get("voice1").toString() : "");
        eventDispatch.setVideo((map.containsKey("video") && Objects.nonNull(map.get("video"))) ? map.get("video").toString() : "");
        eventDispatch.setContent((map.containsKey("content") && Objects.nonNull(map.get("content"))) ? map.get("content").toString() : "");
        eventDispatch.setTransferRemarks((map.containsKey("transferRemarks") && Objects.nonNull(map.get("transferRemarks"))) ? map.get("transferRemarks").toString() : "");
        eventDispatch.setDispatchFrom(adminuser.getId());
        eventDispatch.setTitle(eventList.getEventName());
        eventDispatch.setAssignDate(new Date());
        eventDispatch.setIsEmployeeTransfer(true);
        eventDispatchService.save(eventDispatch);
        try {
            String employeeId = map.get("employeeId").toString();
            String deviceId = valueOperationsService.get("userDeviceId_" + employeeId);
            messageService.send(deviceId, Internationalization.getMessageInternationalization(7000001), eventList.getEventName(), eventList.getId());
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

        systemLogService.log("App event to employee", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping("/map")
    @ResponseBody
    @Authorize(authorizeResources = false)
    public Object map(String id) {
        ResultData resultData = new ResultData();
        EventList eventList = this.eventListService.findById(id);
        if (eventList == null) {
            return resultData;
        }
        Node node = nodeService.findBySourceCode(eventList.getSourceCode());
        if (node != null) {
            NodeVo nodeVo = new NodeVo();
            BeanUtils.copyProperties(node, nodeVo);
            com.pepper.model.emap.map.Map map = mapService.findById(node.getMapId());
            if (map != null) {
                MapVo mapVo = new MapVo();
                BeanUtils.copyProperties(map, mapVo);
                nodeVo.setMap(mapVo);
                NodeTypeVo nodeTypeVo = new NodeTypeVo();
                NodeType nodeType = nodeTypeService.findById(node.getNodeTypeId());
                if (nodeType != null) {
                    BeanUtils.copyProperties(nodeType, nodeTypeVo);
                    nodeTypeVo.setWorkingIconUrl(fileService.getUrl(nodeType.getWorkingIcon()));
                    nodeTypeVo.setStopIconUrl(fileService.getUrl(nodeType.getStopIcon()));
                    nodeTypeVo.setProcessingIconUrl(fileService.getUrl(nodeType.getProcessingIcon()));
                    nodeVo.setNodeType(nodeTypeVo);
                }
                mapVo.setMapImageUrl(mapImageUrlService.findByMapId(map.getId()));
            }
            resultData.setData("node", nodeVo);
        }
        return resultData;
    }

    @RequestMapping("/assist")
    @ResponseBody
    @Authorize(authorizeResources = false)
    public Object assist(@RequestBody String data) throws IOException {
        AdminUser user = (AdminUser) this.getCurrentUser();

        ResultData resultData = new ResultData();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(data);
        String eventListId = jsonNode.get("eventListId").asText("");
        EventList eventList = this.eventListService.findById(eventListId);
        if (eventList == null) {
            resultData.setCode(9000002);
            resultData.setMessage(Internationalization.getMessageInternationalization(9000002));
            return resultData;
        } else if (eventList.getStatus() == "P" || eventList.getStatus() == "B") {
            resultData.setCode(8000002);
            resultData.setMessage(Internationalization.getMessageInternationalization(8000002));
            return resultData;
        }

//		this.eventListAssistService.delete(eventList.getId(), eventList.getCurrentHandleUser(), null);

        if (jsonNode.get("userId").isArray()) {
            if (((ArrayNode) jsonNode.get("userId")).size() <= 0) {
                resultData.setCode(8000005);
                resultData.setMessage(Internationalization.getMessageInternationalization(8000005));
                return resultData;
            }
            for (JsonNode userNode : jsonNode.get("userId")) {
                EventListAssist eventListAssist = new EventListAssist();
                eventListAssist.setAssistRemark(jsonNode.get("assistRemark").asText(""));
                eventListAssist.setEventListId(eventListId);
                eventListAssist.setUserId(userNode.asText(""));
                eventListAssist.setEmployeeId(user.getId());
//				eventListAssist.setIsFinish(false);
                this.eventListAssistService.save(eventListAssist);

//				EventDispatch eventDispatchOld = this.eventDispatchService.findEventDispatch(eventList.getId(),user.getId());
//				
//				EventDispatch eventDispatch = new EventDispatch();
//				eventDispatch.setEventId(eventList.getEventId());
//				eventDispatch.setEventListId(eventList.getId());
//				eventDispatch.setOperator(userNode.asText(""));
//				eventDispatch.setDispatchFrom(eventDispatchOld.getDispatchFrom());
//				eventDispatch.setTitle(eventList.getEventName());
//				eventDispatch.setCreateDate(eventList.getCreateDate());
//				eventDispatch.setAssignDate(new Date());
//				
//				eventDispatchService.save(eventDispatch);
                try {
                    String deviceId = valueOperationsService.get("userDeviceId_" + eventListAssist.getUserId());
                    messageService.send(deviceId, Internationalization.getMessageInternationalization(7000003), eventList.getEventName(), eventList.getId(), true, user.getName());
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } else {
            resultData.setCode(8000005);
            resultData.setMessage(Internationalization.getMessageInternationalization(8000005));
            return resultData;
        }

        systemLogService.log("App event assist", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping("/assist/finish")
    @ResponseBody
    @Authorize(authorizeResources = false)
    public Object assistFinish(@RequestBody String data) throws IOException {
        ResultData resultData = new ResultData();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(data);
        String id = jsonNode.get("id").asText("");
        String image1 = jsonNode.get("image1").asText("");
        String image2 = jsonNode.get("image2").asText("");
        String image3 = jsonNode.get("image3").asText("");
        String voice1 = jsonNode.get("voice1").asText("");
        String video = jsonNode.get("video").asText("");
        EventList eventList = this.eventListService.findById(id);

        AdminUser user = (AdminUser) this.getCurrentUser();
        ActionList actionList = new ActionList();
        actionList.setDepartmentId(user.getDepartmentId());
        actionList.setOperator(user.getId());
        actionList.setEventListId(eventList.getId());
        actionList.setEventId(eventList.getEventId());
        actionList.setStatus("B");
        actionList.setOperatorHelpId(eventList.getHelpId());
        actionList.setOperatorContent(eventList.getContent());
        actionList.setImage1(image1);
        actionList.setImage2(image2);
        actionList.setFinishDate(new Date());
        actionList.setAssignDate(eventList.getAssignDate());
        actionList.setImage3(image3);
        actionList.setVoice1(voice1);
        actionList.setVideo(video);
        actionList.setContent(jsonNode.get("content").asText(""));
//		actionList.setIsAssist(true);
        EventListAssist eventListAssist = this.eventListAssistService.findEventListAssist(id, user.getId(), false);
        if (eventListAssist == null) {
            resultData.setCode(8000003);
            resultData.setMessage(Internationalization.getMessageInternationalization(8000003));
            return resultData;
        } else if (eventListAssist.getIsFinish()) {
            resultData.setCode(8000004);
            resultData.setMessage(Internationalization.getMessageInternationalization(8000004));
            return resultData;
        }
//		actionList.setEventListAssistId(eventListAssist.getId());
        if (jsonNode.has("helpId")) {
            actionList.setHelpId(jsonNode.get("helpId").toString());
        }
        actionListService.save(actionList);

        eventListAssist.setIsFinish(true);
        eventListAssistService.update(eventListAssist);

        systemLogService.log("App event assist finish", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping("/assist/forMe")
    @ResponseBody
    @Authorize(authorizeResources = false)
    public Object assistForMe(Boolean isFinish) throws IOException {
        Pager<EventList> pager = new Pager<EventList>();
        pager = this.eventListService.assistEventList(pager, ((AdminUser) this.getCurrentUser()).getId(), isFinish);
        AdminUser adminUser = (AdminUser) this.getCurrentUser();
        List<EventListVo> returnList = new ArrayList<EventListVo>();
        for (EventList eventList : pager.getResults()) {
            EventListVo eventListVo = this.convertVo(eventList);
            List<EventListAssist> list = this.eventListAssistService.findEventListAssist(eventList.getId(), adminUser.getId());
            if (list.size() > 0) {
                eventListVo.setIsAssistFinish(list.get(0).getIsFinish());
            } else {
                eventListVo.setIsAssistFinish(null);
            }
//			eventListVo.setVideoUrl(this.fileService.getUrl(eventList.getVideo()));
            returnList.add(eventListVo);
        }
        pager.setData("eventList", returnList);
        pager.setResults(null);

        return pager;
    }


    private EventListVo convertVo(EventList eventList) throws IOException {
        AdminUser adminUser = (AdminUser) this.getCurrentUser();
        EventListVo eventListVo = new EventListVo();
        BeanUtils.copyProperties(eventList, eventListVo);
        Node node = this.nodeService.findBySourceCode(eventList.getSourceCode());
        if (node != null) {
            eventListVo.setNodeName(node.getName());
        }
//		eventListVo.setVideoUrl(this.fileService.getUrl(eventList.getVideo()));
        eventListVo.setIsUrgent(eventList.getWarningLevel() >= getUrgentWarningLevel(eventList));
        EventDispatch eventDispatch = this.eventDispatchService.findEventDispatch(eventList.getId(), adminUser.getId());
        if (eventDispatch != null) {
            AdminUser dispatchFrom = this.adminUserService.findById(eventDispatch.getDispatchFrom());
            if (dispatchFrom != null) {
                eventListVo.setAssignHeadPortrait(this.fileService.getUrl(dispatchFrom.getHeadPortrait()));
            } else {
                eventListVo.setAssignHeadPortrait("");
            }
            eventListVo.setDispatchDate(eventDispatch.getCreateDate());
            if (Objects.nonNull(eventDispatch.getIsEmployeeTransfer()) && eventDispatch.getIsEmployeeTransfer()) {
                eventListVo.setIsEmployeeTransfer(eventDispatch.getIsEmployeeTransfer());
                eventListVo.setTransferRemarks(eventDispatch.getTransferRemarks());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("image1", this.fileService.getUrl(eventDispatch.getImage1()));
                map.put("image2", this.fileService.getUrl(eventDispatch.getImage2()));
                map.put("image3", this.fileService.getUrl(eventDispatch.getImage3()));
                map.put("voice1", this.fileService.getUrl(eventDispatch.getVoice1()));
                map.put("video", this.fileService.getUrl(eventDispatch.getVideo()));
                eventListVo.setEmployeeTransferFIle(map);
            }
        }
        eventListVo.setActionList(this.convertActionListVo(this.actionListService.findByEventListId(eventList.getId()), node));
        return eventListVo;
    }

    private List<EventListVo> convertVo(List<EventList> list) throws IOException {
        List<EventListVo> returnList = new ArrayList<EventListVo>();
        for (EventList eventList : list) {
            returnList.add(convertVo(eventList));
        }
        return returnList;
    }

    private Integer getUrgentWarningLevel(EventList eventList) {
        Node node = this.nodeService.findBySourceCode(eventList.getSourceCode());
        EventRule eventRule = this.eventRuleService.findByNodeId(node == null ? "0" : node.getId());
        if (eventRule == null) {
            eventRule = eventRuleService.findByNodeTypeId(node == null ? "0" : node.getNodeTypeId());
        }
        return eventRule == null ? 0 : eventRule.getWarningLevel();
    }


    @RequestMapping("/toMp3")
    @ResponseBody
    public Object toMp3(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
        ResultData resultData = new ResultData();
        Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
        for (String fileName : files.keySet()) {
            MultipartFile file = files.get(fileName);
            File source = new File("\\", "3.amr");
            FileUtils.copyInputStreamToFile(file.getInputStream(), source);
            File target = new File("\\", "‪3.mp3");
            AudioUtils.amrToMp3(source, target);
            FileInputStream io = new FileInputStream(target);
            byte b[] = new byte[(int) target.length()];
            io.read(b);
            String fileId = fileService.addFile(b, "1.mp3");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", fileId);
            map.put("url", fileService.getUrl(fileId));
            io.close();
            resultData.setData(map);
            return resultData;
        }
        return resultData;
    }

    @RequestMapping("/toMeNoFiledCount")
    @ResponseBody
    public Object toMeNoFiledCount() {
        ResultData resultData = new ResultData();
        AdminUser adminuser = (AdminUser) this.getCurrentUser();
        resultData.setData("count", this.eventListService.toMeNoFiledCount(adminuser.getId()));
        return resultData;
    }

    @RequestMapping("/todaySpecialCount")
    @ResponseBody
    public Object todaySpecialCount() {
        ResultData resultData = new ResultData();
        //获取当前时间
        LocalDateTime nowTime = LocalDateTime.now();
        //获取当前日期
        LocalDate nowDate = LocalDate.now();
        //设置零点
        LocalDateTime beginTime = LocalDateTime.of(nowDate, LocalTime.MIN);
        //设置当天的结束时间
        LocalDateTime endTime = LocalDateTime.of(nowDate, LocalTime.MAX);
        resultData.setData("count", this.eventListService.todaySpecialCount(Date.from(beginTime.atZone(ZoneId.systemDefault()).toInstant()), Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant())));
        return resultData;
    }

    @RequestMapping("/todayUrgentCount")
    @ResponseBody
    public Object todayUrgentCount(Date startDate, Date endDate) {
        ResultData resultData = new ResultData();
        //获取当前时间
        LocalDateTime nowTime = LocalDateTime.now();
        //获取当前日期
        LocalDate nowDate = LocalDate.now();
        //设置零点
        LocalDateTime beginTime = LocalDateTime.of(nowDate, LocalTime.MIN);
        //设置当天的结束时间
        LocalDateTime endTime = LocalDateTime.of(nowDate, LocalTime.MAX);
        resultData.setData("count", this.eventListService.todayUrgentCount(Date.from(beginTime.atZone(ZoneId.systemDefault()).toInstant()), Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant())));
        return resultData;
    }

    @RequestMapping("/todayOrdinaryCount")
    @ResponseBody
    public Object todayOrdinaryCount(Date startDate, Date endDate) {
        ResultData resultData = new ResultData();
        //获取当前时间
        LocalDateTime nowTime = LocalDateTime.now();
        //获取当前日期
        LocalDate nowDate = LocalDate.now();
        //设置零点
        LocalDateTime beginTime = LocalDateTime.of(nowDate, LocalTime.MIN);
        //设置当天的结束时间
        LocalDateTime endTime = LocalDateTime.of(nowDate, LocalTime.MAX);
        resultData.setData("count", this.eventListService.todayOrdinaryCount(Date.from(beginTime.atZone(ZoneId.systemDefault()).toInstant()), Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant())));
        return resultData;
    }

    @RequestMapping("/currentMonthCount")
    @ResponseBody
    public Object currentMonthCount() {
        ResultData resultData = new ResultData();
        Date startDate = getBeginDayOfMonth();
        Date endDate = getEndDayOfMonth();
        List<Map<String,Object>> list = eventListService.currentMonthCount(startDate,endDate);
        list.addAll(eventListService.currentMonthCount(startDate,endDate,true));
        resultData.setData("data",list);
        return resultData;
    }

    @RequestMapping("/currentYearCount")
    @ResponseBody
    public Object currentYearCount() {
        ResultData resultData = new ResultData();
        Date startDate = getBeginDayOfYear();
        Date endDate = getEndDayOfYear();
        List<Map<String,Object>> list = eventListService.currentMonthCount(startDate,endDate);
        list.addAll(eventListService.currentMonthCount(startDate,endDate,true));
        resultData.setData("data",list);
        return resultData;
    }

    @RequestMapping("/yearTypeCount")
    @ResponseBody
    public Object yearTypeCount() throws ParseException {
        ResultData resultData = new ResultData();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -12);
        Date m = c.getTime();
        String startDate = sdf.format(m)+"-01 00:00:00";
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
        resultData.setData("special",eventListService.yearTypeCount("isSpecial",sdf1.parse(startDate),new Date()));
        resultData.setData("urgent",eventListService.yearTypeCount("isUrgent",sdf1.parse(startDate),new Date()));
        resultData.setData("ordinary",eventListService.yearTypeCount("ordinary",sdf1.parse(startDate),new Date()));
        return resultData;
    }

    @RequestMapping("/chainRatio")
    @ResponseBody
    public Object chainRatio() throws ParseException {
        ResultData resultData = new ResultData();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date m = c.getTime();
        String startDate = sdf.format(m)+"-01 00:00:00";
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String,Object>> currentMonth = eventListService.currentMonthCount(getBeginDayOfMonth(),new Date());
        List<Map<String,Object>> previousMonth = eventListService.currentMonthCount(sdf1.parse(startDate),new Date());
        List<String> listType = new ArrayList<String>();
        for(Map<String,Object> map  : currentMonth){
            Set<String> key = map.keySet();
            for(String str : key){
                if(str.equals("name")){
                    if(!listType.contains(map.get(str).toString())){
                        listType.add(map.get(str).toString());
                    }
                }
            }
        }
        for(Map<String,Object> map  : previousMonth){
            Set<String> key = map.keySet();
            for(String str : key){
                if(str.equals("name")){
                    if(!listType.contains(map.get(str).toString())){
                        listType.add(map.get(str).toString());
                    }
                }
            }
        }

        Map<String,Object> returnMap = new HashMap<String, Object>();
        for(String type : listType){
            int currentMonthCount = 0 ;
            int previousMonthCount = 0 ;
            for(Map<String,Object> map  : currentMonth){
                if(map.get("name").equals(type)){
                    currentMonthCount = Integer.valueOf(map.get("count").toString());
                }
            }
            for(Map<String,Object> map  : previousMonth){
                if(map.get("name").equals(type)){
                    previousMonthCount = Integer.valueOf(map.get("count").toString());
                }
            }
            int chainRatio=0;
            if(currentMonthCount>0 && previousMonthCount>0){
                chainRatio = (currentMonthCount-previousMonthCount)/previousMonthCount;
            }
            if(currentMonthCount<=0){
                chainRatio = -100;
            }
            if(previousMonthCount<=0){
                chainRatio = 100;
            }
            returnMap.put(type ,chainRatio);
        }

        resultData.setData("data",returnMap);
        return resultData;
    }

    //获取本年的开始时间
    private static Date getBeginDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);
        return getDayStartTime(cal.getTime());
    }

    //获取本年的结束时间
    private static Date getEndDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 31);
        return getDayEndTime(cal.getTime());
    }


    //获取本月的开始时间
    private static Date getBeginDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        return getDayStartTime(calendar.getTime());
    }

    //获取本月的结束时间
    private static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    //获取今年是哪一年
    private static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    //获取本月是哪一月
    private static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

    //获取某个日期的开始时间
    private static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    //获取某个日期的结束时间
    private static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) {
            calendar.setTime(d);
        }
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }
}

