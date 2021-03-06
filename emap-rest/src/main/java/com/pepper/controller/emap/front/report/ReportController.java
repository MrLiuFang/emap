package com.pepper.controller.emap.front.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.ReactiveSetCommands.SRemCommand;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfCell;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.building.BuildingInfo;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventMessage;
import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.report.Report;
import com.pepper.model.emap.report.ReportParameter;
import com.pepper.model.emap.staff.Staff;
import com.pepper.model.emap.vo.AdminUserVo;
import com.pepper.model.emap.vo.BuildingInfoVo;
import com.pepper.model.emap.vo.EventListReportVo;
import com.pepper.model.emap.vo.EventListVo;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.model.emap.vo.NodeTypeVo;
import com.pepper.model.emap.vo.NodeVo;
import com.pepper.model.emap.vo.ReportVo;
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
import com.pepper.service.emap.report.ReportParameterService;
import com.pepper.service.emap.report.ReportService;
import com.pepper.service.emap.staff.StaffService;
import com.pepper.service.file.FileService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;
import com.pepper.util.MapToBeanUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.PdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;

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
	
	@Reference
	private ReportParameterService reportParameterService;
	
	@Resource
	private JdbcTemplate jdbcTemplate;

	private static Connection connection;

	@RequestMapping(value = "/event")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object event(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventStartDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event, Integer warningLevel,
			String node, String nodeTypeId, String mapName, String buildName, String siteName, String operatorId,
			String status, String employeeId,Boolean isUrgent,Boolean isSpecial,String sortBy) {
		systemLogService.log("event report ", this.request.getRequestURL().toString());
		return findEvent(eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId, mapName, buildName,
				siteName, operatorId, status, employeeId, false,null,isUrgent,isSpecial,sortBy);
	}

	private String getChineseFont(){


		//宋体（对应css中的 属性 font-family: SimSun; /*宋体*/）
		String font1 ="C:/Windows/Fonts/simsun.ttc";


		//判断系统类型，加载字体文件
		java.util.Properties prop = System.getProperties();
		String osName = prop.getProperty("os.name").toLowerCase();
		System.out.println(osName);
		if (osName.indexOf("linux")>-1) {
			font1="/usr/share/fonts/simsun.ttc";
		}
		if(!new File(font1).exists()){
			throw new RuntimeException("字体文件不存在,影响导出pdf中文显示！"+font1);
		}
		return font1;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/event/export")
	@ResponseBody
	public void eventExport(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventStartDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event, Integer warningLevel,
			String node, String nodeTypeId, String mapName, String buildName, String siteName, String operatorId,
			String status, String employeeId,Boolean isGroupExport,String groupFilter,String sortFilter,String columnFilter,Boolean isUrgent,Boolean isSpecial
			,String sortBy) throws IOException, DocumentException {
		
//		systemLogService.log("event export report ", this.request.getRequestURL().toString());
		@SuppressWarnings("unchecked")
		List<String> columnFilterList= new ArrayList<>(Arrays.asList(columnFilter.split(",")));
		if(isGroupExport) {
			columnFilterList.remove(groupFilter);
		}
		Pager<EventListVo> pager = (Pager<EventListVo>) findEvent(eventStartDate, eventEndDate, event, warningLevel,
				node, nodeTypeId, mapName, buildName, siteName, operatorId, status, employeeId, true,isGroupExport,isUrgent,isSpecial,sortBy);

//		BaseFont bfChinese = BaseFont.createFont("C:/WINDOWS/Fonts/SIMYOU.TTF", BaseFont.IDENTITY_H,
//				BaseFont.NOT_EMBEDDED);
//		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
		BaseFont bfChinese = BaseFont.createFont(getChineseFont()+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
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
		PdfPTable table ;
//		if(isGroupExport!=null && isGroupExport) 
//		{
//			table = new PdfPTable(columnFilterList.size()>0?columnFilterList.size():11);
//		}else {
//			table = new PdfPTable(columnFilterList.size()>0?columnFilterList.size():11);
//		}
		table = new PdfPTable(columnFilterList.size()>0?columnFilterList.size()+(isGroupExport?1:0):11);
		table.setWidthPercentage(100);
		if(isGroupExport) {
			if(StringUtils.hasText(groupFilter)&&groupFilter.equals("nodeType")) {
				table.addCell(new Paragraph("設備類型", FontChinese));
			}
			if(StringUtils.hasText(groupFilter)&&groupFilter.equals("warningLevel")) {
				table.addCell(new Paragraph("警告級別", FontChinese));
			}
			if(StringUtils.hasText(groupFilter)&&groupFilter.equals("operator")) {
				table.addCell(new Paragraph("操作員", FontChinese));
			}
			if(StringUtils.hasText(groupFilter)&&groupFilter.equals("currentHandleUser")) {
				table.addCell(new Paragraph("處理人", FontChinese));
			}
			for(String str:new String[]{"nodeType","warningLevel","operator","currentHandleUser"}) {
				if(columnFilterList.size()>0&&columnFilterList.contains(str)) {
					if("nodeType".equals(str)) {
						table.addCell(new Paragraph("設備類型", FontChinese));
					}else if("warningLevel".equals(str)) {
						table.addCell(new Paragraph("警告級別", FontChinese));
					}else if("operator".equals(str)) {
						table.addCell(new Paragraph("操作員", FontChinese));
					}else if("currentHandleUser".equals(str)) {
						table.addCell(new Paragraph("處理人", FontChinese));
					}
					
				}
				
			}
			
		}else {
			if(columnFilterList.size()>0&&columnFilterList.contains("nodeType")) {
				table.addCell(new Paragraph("設備類型", FontChinese));
			}
			if(columnFilterList.size()>0&&columnFilterList.contains("warningLevel")) {
				table.addCell(new Paragraph("警告級別", FontChinese));
			}
			if(columnFilterList.size()>0&&columnFilterList.contains("operator")) {
				table.addCell(new Paragraph("操作員", FontChinese));
			}
			if(columnFilterList.size()>0&&columnFilterList.contains("currentHandleUser")) {
				table.addCell(new Paragraph("處理人", FontChinese));
			}
		}
		
		if(columnFilterList.size()>0&&columnFilterList.contains("eventDate")) {
			table.addCell(new Paragraph("發生時間", FontChinese));
		}
		if(columnFilterList.size()>0&&columnFilterList.contains("eventName")) {
			table.addCell(new Paragraph("説明", FontChinese));
		}
		if(columnFilterList.size()>0&&columnFilterList.contains("nodeName")) {
			table.addCell(new Paragraph("設備名稱", FontChinese));
		}
		if(columnFilterList.size()>0&&columnFilterList.contains("isUrgent")) {
			table.addCell(new Paragraph("緊急", FontChinese));
		}
		if(columnFilterList.size()>0&&columnFilterList.contains("isSpecial")) {
			table.addCell(new Paragraph("特急", FontChinese));
		}
		
		if(columnFilterList.size()>0&&columnFilterList.contains("status")) {
			table.addCell(new Paragraph("狀態",FontChinese));
		}
		if(columnFilterList.size()>0&&columnFilterList.contains("mapName")) {
			table.addCell(new Paragraph("地圖",FontChinese));
		}
		List<EventListVo> list = (List<EventListVo>) pager.getData().get("event");
		Map<String ,List<EventListVo>> map = new HashMap<String, List<EventListVo>>();
		if(isGroupExport!=null && isGroupExport) {
			for (EventListVo eventListVo :list) {
				if(StringUtils.hasText(groupFilter)&&groupFilter.equals("nodeType")) {
					if(eventListVo.getNode()!=null && eventListVo.getNode().getNodeType()!=null) {
						List<EventListVo> tempList ;
						if(map.containsKey(eventListVo.getNode().getNodeType().getName())) {
							tempList = map.get(eventListVo.getNode().getNodeType().getName());
							tempList.add(eventListVo);
						}else {
							tempList = new ArrayList<EventListVo>();
							tempList.add(eventListVo);
							map.put(eventListVo.getNode().getNodeType().getName(), tempList);
						}
					}
				}else if(StringUtils.hasText(groupFilter)&&groupFilter.equals("warningLevel")) {
					if(eventListVo.getWarningLevel()!=null) {
						List<EventListVo> tempList ;
						if(map.containsKey(eventListVo.getWarningLevel().toString())) {
							tempList = map.get(eventListVo.getWarningLevel().toString());
							tempList.add(eventListVo);
						}else {
							tempList = new ArrayList<EventListVo>();
							tempList.add(eventListVo);
							map.put(eventListVo.getWarningLevel().toString(), tempList);
						}
					}
				}else if(StringUtils.hasText(groupFilter)&&groupFilter.equals("operator")) {
					if(eventListVo.getOperatorVo()!=null) {
						List<EventListVo> tempList ;
						if(map.containsKey(eventListVo.getOperatorVo().getName())) {
							tempList = map.get(eventListVo.getOperatorVo().getName());
							tempList.add(eventListVo);
						}else {
							tempList = new ArrayList<EventListVo>();
							tempList.add(eventListVo);
							map.put(eventListVo.getOperatorVo().getName(), tempList);
						}
					}
				}else if(StringUtils.hasText(groupFilter)&&groupFilter.equals("currentHandleUser")) {
					if(eventListVo.getCurrentHandleUserVo()!=null) {
						List<EventListVo> tempList ;
						if(map.containsKey(eventListVo.getCurrentHandleUserVo().getName())) {
							tempList = map.get(eventListVo.getCurrentHandleUserVo().getName());
							tempList.add(eventListVo);
						}else {
							tempList = new ArrayList<EventListVo>();
							tempList.add(eventListVo);
							map.put(eventListVo.getCurrentHandleUserVo().getName(), tempList);
						}
					}
				}
			}
		}else {
			map.put("1", list);
		}
		for(String key : map.keySet()){
		
			List<EventListVo> listTemp = map.get(key);
			if(isGroupExport!=null && isGroupExport) 
			{
				PdfPCell cell =new PdfPCell(new Paragraph(key,FontChinese));
				cell.setRowspan(listTemp.size());
				table.addCell(cell);
			}
			for (EventListVo eventListVo :listTemp) {
				if(isGroupExport) {
					for(String str:new String[]{"nodeType","warningLevel","operator","currentHandleUser"}) {
						if(columnFilterList.size()>0&&columnFilterList.contains(str)) {
							if("nodeType".equals(str)) {
								if(eventListVo.getNode()!=null) {
									table.addCell(new Paragraph(eventListVo.getNode().getNodeType()==null?"":eventListVo.getNode().getNodeType().getName(),FontChinese));
								}else {
									table.addCell(new Paragraph("",FontChinese));
								}
							}else if("warningLevel".equals(str)) {
								table.addCell(new Paragraph(eventListVo.getWarningLevel().toString(), FontChinese));
							}else if("operator".equals(str)) {
								table.addCell(new Paragraph(eventListVo.getOperatorVo() == null ? "" : eventListVo.getOperatorVo().getName(), FontChinese));
							}else if("currentHandleUser".equals(str)) {
								table.addCell(new Paragraph(eventListVo.getCurrentHandleUserVo() == null ? "" : eventListVo.getCurrentHandleUserVo().getName(), FontChinese));
							}
						}
					}
				}else {
					if(columnFilterList.size()>0&&columnFilterList.contains("nodeType")) {
						if(eventListVo.getNode()!=null) {
							table.addCell(new Paragraph(eventListVo.getNode().getNodeType()==null?"":eventListVo.getNode().getNodeType().getName(),FontChinese));
						}else {
							table.addCell(new Paragraph("",FontChinese));
						}
					}
					if(columnFilterList.size()>0&&columnFilterList.contains("warningLevel")) {
						table.addCell(new Paragraph(eventListVo.getWarningLevel().toString(), FontChinese));
					}
					if(columnFilterList.size()>0&&columnFilterList.contains("operator")) {
						table.addCell(new Paragraph(eventListVo.getOperatorVo() == null ? "" : eventListVo.getOperatorVo().getName(), FontChinese));
					}
					if(columnFilterList.size()>0&&columnFilterList.contains("currentHandleUser")) {
						table.addCell(new Paragraph(eventListVo.getCurrentHandleUserVo() == null ? "" : eventListVo.getCurrentHandleUserVo().getName(), FontChinese));
					}
				}
				
				if(columnFilterList.size()>0&&columnFilterList.contains("eventDate")) {
					table.addCell(new Paragraph(eventListVo.getEventDate(), FontChinese));
				}
				if(columnFilterList.size()>0&&columnFilterList.contains("eventName")) {
					table.addCell(new Paragraph(eventListVo.getEventName(), FontChinese));	
				}
				if(columnFilterList.size()>0&&columnFilterList.contains("nodeName")) {
					table.addCell(new Paragraph(eventListVo.getNode()==null?"":eventListVo.getNode().getName(), FontChinese));
				}
				if(columnFilterList.size()>0&&columnFilterList.contains("isUrgent")) {
					table.addCell(new Paragraph(eventListVo.getIsUrgent() == null ? "否" : eventListVo.getIsUrgent() ? "是" : "否",
						FontChinese));
				}
				if(columnFilterList.size()>0&&columnFilterList.contains("isSpecial")) {
					table.addCell(new Paragraph(
						eventListVo.getIsSpecial() == null ? "否" : eventListVo.getIsSpecial() ? "是" : "否", FontChinese));
				}
				
				if(columnFilterList.size()>0&&columnFilterList.contains("status")) {
					table.addCell(new Paragraph(eventListVo.getStatus(), FontChinese));
				}
				if(columnFilterList.size()>0&&columnFilterList.contains("mapName")) {
					if(eventListVo.getNode()!=null) {
						if(eventListVo.getNode().getMap()!=null) {
							table.addCell(new Paragraph(eventListVo.getNode().getMap().getName(),FontChinese));
							//table.addCell(new Paragraph(eventListVo.getNode().getMap().getBuild()==null?"":eventListVo.getNode().getMap().getBuild().getName(),FontChinese));
						}else {
							table.addCell(new Paragraph("",FontChinese));
							//table.addCell(new Paragraph("",FontChinese));
						}
					}else {
						table.addCell(new Paragraph("",FontChinese));
						//table.addCell(new Paragraph("",FontChinese));
					}
				}
				
				
				
			}
		}
		document.add(table);
		document.close();
		servletOutputStream.flush();
		servletOutputStream.close();
	}

	@RequestMapping(value = "/openDoor")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object openDoor(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventStartDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event, Integer warningLevel,
			String node, String nodeTypeId, String mapName, String buildName, String siteName, String operatorId,
			String status, String employeeId,Boolean isUrgent,Boolean isSpecial,String sortBy) {
		systemLogService.log("event report ", this.request.getRequestURL().toString());
		return findEvent(eventStartDate, eventEndDate, event, 0, node, "door", mapName, buildName,
				siteName, operatorId, status, employeeId, false,null,isUrgent,isSpecial,sortBy);
	}

	@RequestMapping(value = "/openDoor/export")
	@ResponseBody
	public Object openDoorExport(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventStartDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event, Integer warningLevel,
			String node, String nodeTypeId, String mapName, String buildName, String siteName, String operatorId,
			String status, String employeeId,Boolean isGroupExport,Boolean isUrgent,Boolean isSpecial,String sortBy) throws DocumentException, IOException {
		Pager<EventListVo> pager = (Pager<EventListVo>) findEvent(eventStartDate, eventEndDate, event, 0,
				node, "door", mapName, buildName, siteName, operatorId, status, employeeId, true,isGroupExport,isUrgent,isSpecial,sortBy);

//		BaseFont bfChinese = BaseFont.createFont("C:/WINDOWS/Fonts/SIMYOU.TTF", BaseFont.IDENTITY_H,
//				BaseFont.NOT_EMBEDDED);
//		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
		BaseFont bfChinese = BaseFont.createFont(getChineseFont()+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
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
			String status, String employeeId ,Boolean isUrgent,Boolean isSpecial,String sortBy) {
		systemLogService.log("event report ", this.request.getRequestURL().toString());
		return findEvent(eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId, mapName, buildName,
				siteName, operatorId, status, employeeId, false,null,isUrgent,isSpecial,sortBy);
	}

	@RequestMapping(value = "/employeeHandleEvent")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object employeeHandleEvent(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventStartDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date eventEndDate, String event, Integer warningLevel,
			String node, String nodeTypeId, String mapName, String buildName, String siteName, String operatorId,
			String status, String employeeId,Boolean  isUrgent,Boolean isSpecial,String sortBy) {
		systemLogService.log("event report ", this.request.getRequestURL().toString());
		return findEvent(eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId, mapName, buildName,
				siteName, operatorId, status, employeeId, false,null,isUrgent,isSpecial,sortBy);
	}

	private Object findEvent(Date eventStartDate, Date eventEndDate, String event, Integer warningLevel, String node,
			String nodeTypeId, String mapName, String buildName, String siteName, String operatorId, String status,
			String employeeId, Boolean isExport,Boolean isOrder,Boolean  isUrgent,Boolean isSpecial,String sortBy) {
		Pager<EventList> pager = new Pager<EventList>();
		if (isExport) {
			pager.setPageNo(1);
			pager.setPageSize(Integer.MAX_VALUE);
		}
		pager = this.eventListService.report(pager, eventStartDate, eventEndDate, event, warningLevel, node, nodeTypeId,
				mapName, buildName, siteName, operatorId, status, employeeId,isOrder,sortBy,isSpecial,isUrgent);
		pager.setData("event", convertEventList(pager.getResults(),isUrgent,isSpecial));
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
		BaseFont bfChinese = BaseFont.createFont(getChineseFont()+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
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
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Report report = new Report();
		MapToBeanUtil.convert(report, map);
		report = this.reportService.save(report);
		if(map.containsKey("parameter")) {
			@SuppressWarnings("unchecked")
			List<LinkedHashMap<String, Object>> parameter=(List<LinkedHashMap<String, Object>>) map.get("parameter");
			for(LinkedHashMap<String, Object> obj : parameter) {
				ReportParameter entity = new ReportParameter();
				MapToBeanUtil.convert(entity, obj);
				entity.setReportId(report.getId());
				this.reportParameterService.save(entity);
			}
		}
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		this.reportService.deleteById(map.get("id").toString());
		return add(map);
	}
	
	@RequestMapping(value = "/info")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object info(String id) {
		ResultData resultData = new ResultData();
		Report report = this.reportService.findById(id);
		ReportVo reportVo = new ReportVo();
		BeanUtils.copyProperties(report, reportVo);
		List<ReportParameter> reportParameter = this.reportParameterService.findReportParameter(report.getId());
		reportVo.setReportParameters(reportParameter);
		resultData.setData("report", reportVo);
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
				this.reportService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("node delete", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String name) {
		Pager<Report> pager = new Pager<Report>();
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name", name);
		}
		pager = this.reportService.findNavigator(pager);
		List<Report>  list = pager.getResults();
		List<ReportVo> retuenList = new ArrayList<ReportVo>();
		for(Report report : list) {
			ReportVo reportVo = new ReportVo();
			BeanUtils.copyProperties(report, reportVo);
			List<ReportParameter> reportParameter = this.reportParameterService.findReportParameter(report.getId());
			reportVo.setReportParameters(reportParameter);
			retuenList.add(reportVo);
		}
		pager.setResults(null);
		pager.setData("report", retuenList);
		return pager;
	}
	
	@RequestMapping(value = "/export")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object export(String id) throws JRException, SQLException, IOException, ParseException {
		Report report = this.reportService.findById(id);
		String fileId = report.getFile();
		com.pepper.model.file.File file = this.fileService.getFile(fileId);
		File reportFile = new File("D:"+file.getUrl());
//		File reportFile = new File(file.getUrl());
		Map<String,Object> parameters = new HashMap<String,Object>();
		List<ReportParameter> list = this.reportParameterService.findReportParameter(report.getId());
		for(ReportParameter reportParameter : list) {
			String key = reportParameter.getParameter();
			String value = this.request.getParameter(key);
			if(reportParameter.getType()==1) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				parameters.put(key,dateFormat.parse(value) );
			}else if(reportParameter.getType()==2) {
				parameters.put(key,value );
			}else if(reportParameter.getType()==3) {
				parameters.put(key, Integer.valueOf(value));
			}
		}
		if(Objects.isNull(this.connection) || connection.isClosed()){
			connection = jdbcTemplate.getDataSource().getConnection();
		}
		JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, connection);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(report.getName()+".pdf", "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		JRPdfExporter exporter = new JRPdfExporter();
		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		exporter.setConfiguration(configuration);
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
        outputStream.flush();
        outputStream.close();
		return null;
	}


	@RequestMapping(value = "/test")
	@ResponseBody
	public Object test() throws JRException, SQLException, IOException {
		File reportFile = new File("/home/mr.liu/JaspersoftWorkspace/MyReports/test.jasper");
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("event_id", "lif_20190723151512_235618");
		if(Objects.isNull(this.connection) || connection.isClosed()){
			connection = jdbcTemplate.getDataSource().getConnection();
		}
		JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, connection);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("test.pdf", "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		JRPdfExporter exporter = new JRPdfExporter();
		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		exporter.setConfiguration(configuration);
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();

        outputStream.close();
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

	private List<EventListVo> convertEventList(List<EventList> list,Boolean  isUrgent,Boolean isSpecial) {
		List<EventListVo> returnList = new ArrayList<EventListVo>();
		for (EventList obj : list) {
			EventListVo eventListVo = new EventListVo();
			BeanUtils.copyProperties(obj, eventListVo);
//			eventListVo.setIsUrgent(obj.getWarningLevel()>=getUrgentWarningLevel(obj));
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
//		setEventMessage(returnList);
		
		return returnList;
	}
	
	private Integer getUrgentWarningLevel(EventList eventList) {
		Node node = this.nodeService.findBySourceCode(eventList.getSourceCode());
		EventRule eventRule = this.eventRuleService.findByNodeId(node==null?"0":node.getId());
		if(eventRule == null) {
			eventRule = eventRuleService.findByNodeTypeId(node==null?"0":node.getNodeTypeId());
		}
		return eventRule==null?0:eventRule.getWarningLevel();
	}

//	private void setEventMessage(List<EventListVo> list) {
//		for (EventListVo eventListVo : list) {
//			List<EventMessage> listEventMessage = this.eventMessageService.findEventMessage(eventListVo.getId());
//			eventListVo.setEventMessage(listEventMessage);
//		}
//	}

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
					nodeTypeVo.setProcessingIconUrl(fileService.getUrl(nodeType.getProcessingIcon()));
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
