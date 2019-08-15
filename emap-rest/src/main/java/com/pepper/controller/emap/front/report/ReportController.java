package com.pepper.controller.emap.front.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.building.BuildingInfo;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventMessage;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.staff.Staff;
import com.pepper.model.emap.vo.AdminUserVo;
import com.pepper.model.emap.vo.BuildingInfoVo;
import com.pepper.model.emap.vo.EventListReportVo;
import com.pepper.model.emap.vo.EventListVo;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.model.emap.vo.NodeTypeVo;
import com.pepper.model.emap.vo.NodeVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.emap.building.BuildingInfoService;
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
import com.pepper.service.emap.report.ReportService;
import com.pepper.service.emap.staff.StaffService;
import com.pepper.service.file.FileService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;

@Controller()
@RequestMapping(value = "/front/report")
public class ReportController extends BaseControllerImpl implements BaseController {

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
	private BuildingInfoService buildingInfoService;
	
	@Reference
	private ReportService reportService;

	@RequestMapping(value = "/event")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object event(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventStartDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event, Integer warningLevel,
			String node, String nodeTypeId, String mapName, String buildName, String siteName, String operatorId,
			String status, String employeeId) {
		systemLogService.log("event report ", this.request.getRequestURL().toString());
		return findEvent(eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId, mapName, buildName,
				siteName, operatorId, status, employeeId, false);
	}

	@RequestMapping(value = "/event/export")
	@ResponseBody
	public Object eventExport(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventStartDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event, Integer warningLevel,
			String node, String nodeTypeId, String mapName, String buildName, String siteName, String operatorId,
			String status, String employeeId) throws IOException, DocumentException {
//		systemLogService.log("event export report ", this.request.getRequestURL().toString());
		@SuppressWarnings("unchecked")
		Pager<EventListVo> pager = (Pager<EventListVo>) findEvent(eventStartDate, eventEndDate, event, warningLevel,
				node, nodeTypeId, mapName, buildName, siteName, operatorId, status, employeeId, true);

//		BaseFont bfChinese = BaseFont.createFont("C:/WINDOWS/Fonts/SIMYOU.TTF", BaseFont.IDENTITY_H,
//				BaseFont.NOT_EMBEDDED);
//		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
		BaseFont bfChinese = BaseFont.createFont("MicrosoftYaHei.ttf", BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
		Font FontChinese = new Font(bfChinese);

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("事件清單.pdf", "UTF-8"));
		Document document = new Document();
		Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
		pageSize.rotate();
		document.setPageSize(pageSize);
		ServletOutputStream servletOutputStream = response.getOutputStream();
		PdfWriter.getInstance(document, servletOutputStream);
		document.open();
		document.addTitle("事件清單");
		Paragraph paragraph = new Paragraph("事件清單", FontChinese);
		paragraph.setSpacingAfter(50);
		paragraph.setIndentationLeft(350);
		document.add(paragraph);
		PdfPTable table = new PdfPTable(7);
		table.setWidthPercentage(100);
		table.addCell(new Paragraph("發生時間", FontChinese));
		table.addCell(new Paragraph("説明", FontChinese));
		table.addCell(new Paragraph("設備名稱", FontChinese));
		table.addCell(new Paragraph("警告級別", FontChinese));
		table.addCell(new Paragraph("緊急", FontChinese));
		table.addCell(new Paragraph("特急", FontChinese));
		table.addCell(new Paragraph("操作員", FontChinese));
		table.addCell(new Paragraph("處理人",FontChinese));
		table.addCell(new Paragraph("狀態",FontChinese));
		table.addCell(new Paragraph("地圖",FontChinese));
		table.addCell(new Paragraph("設備類型", FontChinese));

		for (EventListVo eventListVo : (List<EventListVo>) pager.getData().get("event")) {
			table.addCell(new Paragraph(eventListVo.getEventDate(), FontChinese));
			table.addCell(new Paragraph(eventListVo.getEventName(), FontChinese));			
			table.addCell(new Paragraph(eventListVo.getNode()==null?"":eventListVo.getNode().getName(), FontChinese));
			table.addCell(new Paragraph(eventListVo.getWarningLevel().toString(), FontChinese));
			table.addCell(new Paragraph(eventListVo.getIsUrgent() == null ? "否" : eventListVo.getIsUrgent() ? "是" : "否",
					FontChinese));
			table.addCell(new Paragraph(
					eventListVo.getIsSpecial() == null ? "否" : eventListVo.getIsSpecial() ? "是" : "否", FontChinese));
			table.addCell(new Paragraph(eventListVo.getOperatorVo() == null ? "" : eventListVo.getOperatorVo().getName(), FontChinese));
			table.addCell(new Paragraph(eventListVo.getCurrentHandleUserVo() == null ? "" : eventListVo.getCurrentHandleUserVo().getName(), FontChinese));
			if(eventListVo.getNode()!=null) {
				if(eventListVo.getNode().getMap()!=null) {
					table.addCell(new Paragraph(eventListVo.getNode().getMap().getName(),FontChinese));
					table.addCell(new Paragraph(eventListVo.getNode().getMap().getBuild()==null?"":eventListVo.getNode().getMap().getBuild().getName(),FontChinese));
				}else {
					table.addCell(new Paragraph("",FontChinese));
					table.addCell(new Paragraph("",FontChinese));
				}
			}else {
				table.addCell(new Paragraph("",FontChinese));
				table.addCell(new Paragraph("",FontChinese));
			}
			if(eventListVo.getNode()!=null) {
				table.addCell(new Paragraph(eventListVo.getNode().getNodeType()==null?"":eventListVo.getNode().getName(),FontChinese));
			}else {
				table.addCell(new Paragraph("",FontChinese));
			}
			
			
		}
		document.add(table);
		document.close();
		servletOutputStream.flush();
		servletOutputStream.close();
		return null;
	}

	@RequestMapping(value = "/openDoor")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object openDoor(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventStartDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event, Integer warningLevel,
			String node, String nodeTypeId, String mapName, String buildName, String siteName, String operatorId,
			String status, String employeeId) {
		systemLogService.log("event report ", this.request.getRequestURL().toString());
		return findEvent(eventStartDate, eventEndDate, event, 0, node, "door", mapName, buildName,
				siteName, operatorId, status, employeeId, false);
	}

	@RequestMapping(value = "/openDoor/export")
	@ResponseBody
	public Object openDoorExport(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventStartDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event, Integer warningLevel,
			String node, String nodeTypeId, String mapName, String buildName, String siteName, String operatorId,
			String status, String employeeId) throws DocumentException, IOException {
		Pager<EventListVo> pager = (Pager<EventListVo>) findEvent(eventStartDate, eventEndDate, event, 0,
				node, "door", mapName, buildName, siteName, operatorId, status, employeeId, false);

//		BaseFont bfChinese = BaseFont.createFont("C:/WINDOWS/Fonts/SIMYOU.TTF", BaseFont.IDENTITY_H,
//				BaseFont.NOT_EMBEDDED);
//		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
		BaseFont bfChinese = BaseFont.createFont("MicrosoftYaHei.ttf", BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
		Font FontChinese = new Font(bfChinese);

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("門禁記錄.pdf", "UTF-8"));
		Document document = new Document();
		Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
		pageSize.rotate();
		document.setPageSize(pageSize);
		ServletOutputStream servletOutputStream = response.getOutputStream();
		PdfWriter.getInstance(document, servletOutputStream);
		document.open();
		document.addTitle("門禁記錄");
		Paragraph paragraph = new Paragraph("門禁記錄", FontChinese);
		paragraph.setSpacingAfter(50);
		paragraph.setIndentationLeft(350);
		document.add(paragraph);
		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100);
		table.addCell(new Paragraph("事件時間", FontChinese));
		table.addCell(new Paragraph("設備編碼", FontChinese));
		table.addCell(new Paragraph("設備名稱", FontChinese));
		table.addCell(new Paragraph("設備來源", FontChinese));
		table.addCell(new Paragraph("設備來源編碼", FontChinese));
//		table.addCell(new Paragraph("建築名稱", FontChinese));
		table.addCell(new Paragraph("員工", FontChinese));

		for (EventListVo eventListVo : (List<EventListVo>) pager.getData().get("event")) {
			table.addCell(new Paragraph(eventListVo.getEventDate(), FontChinese));
			table.addCell(new Paragraph(eventListVo.getNode().getCode(), FontChinese));
			table.addCell(new Paragraph(eventListVo.getNode().getName(), FontChinese));
			table.addCell(new Paragraph(eventListVo.getNode().getSource(), FontChinese));
			table.addCell(new Paragraph(eventListVo.getNode().getSourceCode(), FontChinese));
//			table.addCell(new Paragraph(eventListVo.getNode().getMap().getName(), FontChinese));
//			table.addCell(new Paragraph(eventListVo.getNode().getMap().getBuild().getName(), FontChinese));
			table.addCell(new Paragraph(eventListVo.getStaff() == null ? "" : eventListVo.getStaff().getName()));
		}
		document.add(table);
		document.close();
		servletOutputStream.flush();
		servletOutputStream.close();
		return null;
	}

	@RequestMapping(value = "/nodeEvent")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object nodeEvent(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventStartDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event, Integer warningLevel,
			String node, String nodeTypeId, String mapName, String buildName, String siteName, String operatorId,
			String status, String employeeId) {
		systemLogService.log("event report ", this.request.getRequestURL().toString());
		return findEvent(eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId, mapName, buildName,
				siteName, operatorId, status, employeeId, false);
	}

	@RequestMapping(value = "/employeeHandleEvent")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object employeeHandleEvent(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventStartDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event, Integer warningLevel,
			String node, String nodeTypeId, String mapName, String buildName, String siteName, String operatorId,
			String status, String employeeId) {
		systemLogService.log("event report ", this.request.getRequestURL().toString());
		return findEvent(eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId, mapName, buildName,
				siteName, operatorId, status, employeeId, false);
	}

	private Object findEvent(Date eventStartDate, Date eventEndDate, String event, Integer warningLevel, String node,
			String nodeTypeId, String mapName, String buildName, String siteName, String operatorId, String status,
			String employeeId, Boolean isExport) {
		Pager<EventList> pager = new Pager<EventList>();
		if (isExport) {
			pager.setPageNo(1);
			pager.setPageSize(Integer.MAX_VALUE);
		}
		pager = this.eventListService.report(pager, eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId,
				mapName, buildName, siteName, operatorId, status, employeeId);
		pager.setData("event", convertEventList(pager.getResults()));
		pager.setResults(null);

		return pager;
	}
	
	

	@RequestMapping(value = "/nodeTypeEventStat")
	@ResponseBody
	public Object nodeTypeEventStat(String nodeTypeId,String mapId) {
		return findNodeTypeEventStat(nodeTypeId,mapId);
	}
	
	@RequestMapping(value = "/nodeTypeEventStat/export")
	@ResponseBody
	public Object nodeTypeEventStatExport(String nodeTypeId,String mapId) throws DocumentException, IOException {
		
//		BaseFont bfChinese = BaseFont.createFont("C:/WINDOWS/Fonts/SIMYOU.TTF", BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
//		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
		BaseFont bfChinese = BaseFont.createFont("MicrosoftYaHei.ttf", BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
		Font FontChinese = new Font(bfChinese);

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("設備事件統計.pdf", "UTF-8"));
		Document document = new Document();
		Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
		pageSize.rotate();
		document.setPageSize(pageSize);
		ServletOutputStream servletOutputStream = response.getOutputStream();
		PdfWriter.getInstance(document, servletOutputStream);
		document.open();
		document.addTitle("設備事件統計");
		Paragraph paragraph = new Paragraph("設備事件統計", FontChinese);
		paragraph.setSpacingAfter(50);
		paragraph.setIndentationLeft(350);
		document.add(paragraph);
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100);
		table.addCell(new Paragraph("設備類型名稱", FontChinese));
		table.addCell(new Paragraph("地圖名稱", FontChinese));
		table.addCell(new Paragraph("設備數量", FontChinese));
		table.addCell(new Paragraph("當天發生事件的設備數量", FontChinese));
		table.addCell(new Paragraph("一周内發生事件的設備數量", FontChinese));
		List<Map<String,Object>> list = findNodeTypeEventStat(nodeTypeId,mapId).getResults();
		for (Map<String,Object> map : list) {
			table.addCell(new Paragraph(map.get("nodeTypeName").toString(), FontChinese));
			table.addCell(new Paragraph(map.get("mapName").toString(), FontChinese));
			table.addCell(new Paragraph(map.get("nodeCount").toString(), FontChinese));
			table.addCell(new Paragraph(map.get("dayEventCount").toString(), FontChinese));
			table.addCell(new Paragraph(map.get("weekEventCount").toString(), FontChinese));
			
		}
		document.add(table);
		document.close();
		servletOutputStream.flush();
		servletOutputStream.close();
		return null;
	}
	
	private Pager<Map<String,Object>> findNodeTypeEventStat(String nodeTypeId,String mapId){
		Pager<Map<String,Object>> pager = new Pager<Map<String,Object>>();
		pager = reportService.findNodeTypeAndMap(pager,nodeTypeId, mapId);
		List<Map<String,Object>> retusnList = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map : pager.getResults()){
			Map<String,Object> returnMap = new HashMap<String, Object>();
			returnMap.put("nodeTypeName", map.get("nodeTypeName"));
			returnMap.put("mapName", map.get("mapName"));
			returnMap.put("nodeCount", reportService.findNodeCout(map.get("nodeTypeId").toString(), map.get("mapId").toString()));
			returnMap.put("dayEventCount", reportService.findNodeCout(map.get("nodeTypeId").toString(), map.get("mapId").toString()));
			returnMap.put("weekEventCount", reportService.findNodeCout(map.get("nodeTypeId").toString(), map.get("mapId").toString()));
			retusnList.add(returnMap);
		}
		pager.setResults(retusnList);
		return pager;
	}

	private List<EventListVo> convertEventList(List<EventList> list) {
		List<EventListVo> returnList = new ArrayList<EventListVo>();
		for (EventList obj : list) {
			EventListVo eventListVo = new EventListVo();
			BeanUtils.copyProperties(obj, eventListVo);
			setNode(obj, eventListVo);
			if (StringUtils.hasText(obj.getOperator())) {
				AdminUser operatorUser = this.adminUserService.findById(obj.getOperator());
				if (operatorUser != null) {
					AdminUserVo adminUserVo = new AdminUserVo();
					BeanUtils.copyProperties(operatorUser, adminUserVo);
					adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(operatorUser.getHeadPortrait()));
					eventListVo.setOperatorVo(adminUserVo);
				}
			}
			if (StringUtils.hasText(obj.getCurrentHandleUser())) {
				AdminUser currentHandleUser = this.adminUserService.findById(obj.getCurrentHandleUser());
				if (currentHandleUser != null) {
					AdminUserVo adminUserVo = new AdminUserVo();
					BeanUtils.copyProperties(currentHandleUser, adminUserVo);
					adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(currentHandleUser.getHeadPortrait()));
					eventListVo.setCurrentHandleUserVo(adminUserVo);
				}
			}

			if (StringUtils.hasText(obj.getIdCard())) {
				List<Staff> staffList = this.staffService.findByIdCard(obj.getIdCard());
				Staff staff = staffList.size() > 0 ? staffList.get(0) : null;
				if (staff != null) {
					eventListVo.setStaff(staff);
				}
			}

			returnList.add(eventListVo);
		}
		setEventMessage(returnList);
		return returnList;
	}

	private void setEventMessage(List<EventListVo> list) {
		for (EventListVo eventListVo : list) {
			List<EventMessage> listEventMessage = this.eventMessageService.findEventMessage(eventListVo.getId());
			eventListVo.setEventMessage(listEventMessage);
		}
	}

	private void setNode(EventList eventList, EventListVo eventListVo) {
		Node node = nodeService.findBySourceCode(
				StringUtils.hasText(eventList.getSourceCode()) ? eventList.getSourceCode() : "111111111");
		if (node != null) {
			NodeVo nodeVo = new NodeVo();
			BeanUtils.copyProperties(node, nodeVo);
			if (node.getStatus() != null) {
				nodeVo.setStatusCode(node.getStatus().getName());
			}
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
					nodeVo.setNodeType(nodeTypeVo);
				}
				mapVo.setMapImageUrl(mapImageUrlService.findByMapId(map.getId()));
				BuildingInfo buildingInfo = buildingInfoService.findById(mapVo.getBuildId());
				if (buildingInfo != null) {
					BuildingInfoVo buildingInfoVo = new BuildingInfoVo();
					BeanUtils.copyProperties(buildingInfo, buildingInfoVo);
					mapVo.setBuild(buildingInfoVo);
				}
			}

			eventListVo.setNode(nodeVo);
		}
	}

}
