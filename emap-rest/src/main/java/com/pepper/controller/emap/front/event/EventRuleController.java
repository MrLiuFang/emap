package com.pepper.controller.emap.front.event;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletOutputStream;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.ExcelColumn;
import com.pepper.controller.emap.util.ExportExcelUtil;
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.event.EventRule;
import com.pepper.model.emap.event.HelpList;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.site.SiteInfo;
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

	@RequestMapping(value = "/export")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public void export(String nodeId, Integer warningLevel, String keyWord) throws IOException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		systemLogService.log("help export", this.request.getRequestURL().toString());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xlsx");
		response.setHeader("Content-Disposition",
				"attachment;filename=" + URLEncoder.encode("eventRule.xlsx", "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		Pager<EventRule> pager = getPager(nodeId, warningLevel, true);
		List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
		excelColumn.add(ExcelColumn.build("設備", "node.name"));
		excelColumn.add(ExcelColumn.build("設備類型", "nodeType.name"));
		excelColumn.add(ExcelColumn.build("告警級別", "warningLevel"));
		excelColumn.add(ExcelColumn.build("超時時長", "timeOut"));
		excelColumn.add(ExcelColumn.build("自動派單開始時間", "fromDateTime"));
		excelColumn.add(ExcelColumn.build("自動派單結束時間", "toDateTime"));
		excelColumn.add(ExcelColumn.build("部門", "department.name"));
		excelColumn.add(ExcelColumn.build("特級告警級別", "specialWarningLevel"));
		excelColumn.add(ExcelColumn.build("特級處理部門", "specialDepartment.name"));
		excelColumn.add(ExcelColumn.build("接受短信號碼", "sMSReceiver"));
		excelColumn.add(ExcelColumn.build("電郵地址", "emailAccount"));
		excelColumn.add(ExcelColumn.build("短信内容", "sMSContent"));
		excelColumn.add(ExcelColumn.build("電郵標題", "emailTitle"));
		excelColumn.add(ExcelColumn.build("電郵内容", "emailContent"));
		new ExportExcelUtil().export((Collection<?>) pager.getData().get("eventRule"), outputStream, excelColumn);
	}

	private Pager<EventRule> getPager(String nodeId, Integer warningLevel, Boolean isExport) {
		Pager<EventRule> pager = new Pager<EventRule>();
		if (Objects.equals(isExport, true)) {
			pager.setPageNo(1);
			pager.setPageSize(Integer.MAX_VALUE);
		}
		if (StringUtils.hasText(nodeId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL + "_nodeId", nodeId);
		}
		if (warningLevel != null) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL + "_warningLevel", warningLevel);
		}
		pager = eventRuleService.findNavigator(pager);
		List<EventRule> list = pager.getResults();
		List<EventRuleVo> returnList = new ArrayList<EventRuleVo>();
		for (EventRule eventRule : list) {
			returnList.add(this.convertEventRule(eventRule));
		}
		pager.setData("eventRule", returnList);
		pager.setResults(null);
		return pager;
	}

	@RequestMapping(value = "/import")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importStaff(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		List<EventRule> list = new ArrayList<EventRule>();
		for (String fileName : files.keySet()) {
			MultipartFile file = files.get(fileName);
			Workbook wookbook = null;
			try {
				if (isExcel2003(fileName)) {
					wookbook = new HSSFWorkbook(file.getInputStream());
				} else if (isExcel2007(fileName)) {
					wookbook = new XSSFWorkbook(file.getInputStream());
				}
			} catch (IOException e) {
			}

			Sheet sheet = wookbook.getSheetAt(0);
			Row rowHead = sheet.getRow(0);
			int totalRowNum = sheet.getLastRowNum();
			if (!check(sheet.getRow(0))) {
				resultData.setMessage("数据错误！");
				return resultData;
			}
			for (int i = 1; i <= totalRowNum; i++) {
				Row row = sheet.getRow(i);
				EventRule eventRule = new EventRule();
				String nodeName = getCellValue(row.getCell(0)).toString();
				String nodeTypeName = getCellValue(row.getCell(1)).toString();
				if (StringUtils.hasText(nodeName)) {
					Node node = this.nodeService.findByName(nodeName);
					if (node != null) {
						eventRule.setNodeId(node.getId());
					}
				} else {
					NodeType nodeType = this.nodeTypeService.findByName(nodeTypeName);
					if (nodeType != null) {
						eventRule.setNodeTypeId(nodeType.getId());
					}
				}
				eventRule.setWarningLevel(
						Integer.valueOf(getCellValue(row.getCell(2)).toString().replaceAll("(\\.(\\d*))", "")));
				eventRule.setTimeOut(
						Integer.valueOf(getCellValue(row.getCell(3)).toString().replaceAll("(\\.(\\d*))", "")));
				eventRule.setFromDateTime(getCellValue(row.getCell(4)).toString());
				eventRule.setToDateTime(getCellValue(row.getCell(5)).toString());
				String departmentName = getCellValue(row.getCell(6)).toString();
				List<Department> listDepartment = this.departmentService.findByName(departmentName);
				Department department = listDepartment.size() > 0 ? listDepartment.get(0) : null;
				if (department != null) {
					eventRule.setDepartmentId(department.getId());
				}
				eventRule.setSpecialWarningLevel(
						Integer.valueOf(getCellValue(row.getCell(7)).toString().replaceAll("(\\.(\\d*))", "")));
				String specialDepartmentName = getCellValue(row.getCell(8)).toString();
				List<Department> listSpecialDepartment = this.departmentService.findByName(specialDepartmentName);
				Department specialDepartment = listSpecialDepartment.size() > 0 ? listSpecialDepartment.get(0) : null;
				if (specialDepartment != null) {
					eventRule.setSpecialDepartmentId(specialDepartment.getId());
				}
				eventRule.setsMSReceiver(getCellValue(row.getCell(9)).toString());
				eventRule.setEmailAccount(getCellValue(row.getCell(10)).toString());
				eventRule.setsMSContent(getCellValue(row.getCell(11)).toString());
				eventRule.setEmailTitle(getCellValue(row.getCell(12)).toString());
				eventRule.setEmailContent(getCellValue(row.getCell(13)).toString());
				list.add(eventRule);
			}
			this.eventRuleService.saveAll(list);
		}
		systemLogService.log("import event rule");
		return resultData;
	}

	private boolean isExcel2003(String filePath) {
		return StringUtils.hasText(filePath) && filePath.endsWith(".xls");
	}

	private boolean isExcel2007(String filePath) {
		return StringUtils.hasText(filePath) && filePath.endsWith(".xlsx");
	}

	private Boolean check(Row row) {
		if (!getCellValue(row.getCell(0)).toString().equals("node")) {
			return false;
		}
		if (!getCellValue(row.getCell(1)).toString().equals("nodeType")) {
			return false;
		}
		if (!getCellValue(row.getCell(2)).toString().equals("warningLevel")) {
			return false;
		}
		if (!getCellValue(row.getCell(3)).toString().equals("timeOut")) {
			return false;
		}
		if (!getCellValue(row.getCell(4)).toString().equals("fromDateTime")) {
			return false;
		}
		if (!getCellValue(row.getCell(5)).toString().equals("toDateTime")) {
			return false;
		}
		if (!getCellValue(row.getCell(6)).toString().equals("department")) {
			return false;
		}
		if (!getCellValue(row.getCell(7)).toString().equals("specialWarningLevel")) {
			return false;
		}
		if (!getCellValue(row.getCell(8)).toString().equals("specialDepartment")) {
			return false;
		}
		if (!getCellValue(row.getCell(9)).toString().equals("sMSReceiver")) {
			return false;
		}
		if (!getCellValue(row.getCell(10)).toString().equals("emailAccount")) {
			return false;
		}
		if (!getCellValue(row.getCell(11)).toString().equals("sMSContent")) {
			return false;
		}
		if (!getCellValue(row.getCell(12)).toString().equals("emailTitle")) {
			return false;
		}
		if (!getCellValue(row.getCell(13)).toString().equals("emailContent")) {
			return false;
		}
		return true;
	}

	private Object getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		Object object = "";
		switch (cell.getCellType()) {
		case STRING:
			object = cell.getStringCellValue();
			break;
		case NUMERIC:
			object = cell.getNumericCellValue();
			break;
		case BOOLEAN:
			object = cell.getBooleanCellValue();
			break;
		default:
			break;
		}
		return object;
	}

	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String nodeId, Integer warningLevel) {

		systemLogService.log("get event rule list", this.request.getRequestURL().toString());
		return getPager(nodeId, warningLevel, false);
	}

	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String, Object> map) {
		ResultData resultData = new ResultData();
		EventRule eventRule = new EventRule();
		MapToBeanUtil.convert(eventRule, map);

		if (StringUtils.hasText(eventRule.getNodeId())) {
			if (this.eventRuleService.findByNodeId(eventRule.getNodeId()) != null) {
				resultData.setMessage(Internationalization.getMessageInternationalization(5000001));
				resultData.setCode(5000001);
				return resultData;
			}
		}

		if (StringUtils.hasText(eventRule.getNodeTypeId())) {
			if (eventRuleService.findByNodeTypeId(eventRule.getNodeTypeId()) != null) {
				resultData.setMessage(Internationalization.getMessageInternationalization(5000002));
				resultData.setCode(5000002);
				return resultData;
			}
		}

		if (eventRule.getWarningLevel() != null && eventRule.getSpecialWarningLevel() != null
				&& eventRule.getSpecialWarningLevel() > 0) {
			if (eventRule.getWarningLevel() >= eventRule.getSpecialWarningLevel()) {
				resultData.setMessage(Internationalization.getMessageInternationalization(6000001));
				resultData.setCode(6000001);
				return resultData;
			} else {
				if (eventRule.getSpecialWarningLevel() > 0
						&& !StringUtils.hasText(eventRule.getSpecialDepartmentId())) {
					resultData.setMessage(Internationalization.getMessageInternationalization(6000002));
					resultData.setCode(6000002);
					return resultData;
				}
			}
		}

		eventRuleService.save(eventRule);
		systemLogService.log("event rule add", this.request.getRequestURL().toString());
		return resultData;
	}

	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String, Object> map) {
		ResultData resultData = new ResultData();
		EventRule eventRule = new EventRule();
		MapToBeanUtil.convert(eventRule, map);

		if (StringUtils.hasText(eventRule.getNodeId())) {
			EventRule oldEventRule = this.eventRuleService.findByNodeId(eventRule.getNodeId());
			if (oldEventRule != null && oldEventRule.getId() != null && eventRule.getId() != null
					&& !oldEventRule.getId().equals(eventRule.getId())) {
				resultData.setMessage(Internationalization.getMessageInternationalization(5000001));
				resultData.setCode(5000001);
				return resultData;
			}
		} else {
			eventRule.setNodeId("");
		}

		if (StringUtils.hasText(eventRule.getNodeTypeId())) {
			EventRule oldEventRule1 = this.eventRuleService.findByNodeTypeId(eventRule.getNodeTypeId());
			if (oldEventRule1 != null && oldEventRule1.getId() != null && eventRule.getId() != null
					&& !oldEventRule1.getId().equals(eventRule.getId())) {
				resultData.setMessage(Internationalization.getMessageInternationalization(5000002));
				resultData.setCode(5000002);
				return resultData;
			}
		} else {
			eventRule.setNodeTypeId("");
		}

		if (eventRule.getWarningLevel() != null && eventRule.getSpecialWarningLevel() != null
				&& eventRule.getSpecialWarningLevel() > 0) {
			if (eventRule.getWarningLevel() >= eventRule.getSpecialWarningLevel()) {
				resultData.setMessage(Internationalization.getMessageInternationalization(6000001));
				resultData.setCode(6000001);
				return resultData;
			} else {
				if (eventRule.getSpecialWarningLevel() > 0
						&& !StringUtils.hasText(eventRule.getSpecialDepartmentId())) {
					resultData.setMessage(Internationalization.getMessageInternationalization(6000002));
					resultData.setCode(6000002);
					return resultData;
				}
			}
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
		resultData.setData("eventRule", convertEventRule(eventRule));
		systemLogService.log("get event rule info", this.request.getRequestURL().toString());
		return resultData;
	}

	@RequestMapping(value = "/delete")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object delete(@RequestBody String str) throws IOException {
		ResultData resultData = new ResultData();
		if (!StringUtils.hasText(str)) {
			return resultData;
		}
		JsonNode jsonNode = new ObjectMapper().readTree(str);
		if (!jsonNode.has("id")) {
			return resultData;
		}
		ArrayNode arrayNode = (ArrayNode) jsonNode.get("id");
		for (int i = 0; i < arrayNode.size(); i++) {
			String id = arrayNode.get(i).asText();
			try {
				eventRuleService.deleteById(id);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("event rule delete", this.request.getRequestURL().toString());
		return resultData;
	}

	private EventRuleVo convertEventRule(EventRule eventRule) {
		EventRuleVo eventRuleVo = new EventRuleVo();
		if (eventRule != null) {
			BeanUtils.copyProperties(eventRule, eventRuleVo);
			if (StringUtils.hasText(eventRule.getNodeId())) {
				eventRuleVo.setNode(nodeService.findById(eventRule.getNodeId()));
			}
			if (StringUtils.hasText(eventRuleVo.getDepartmentId())) {
				Department department = departmentService.findById(eventRuleVo.getDepartmentId());
				eventRuleVo.setDepartment(department);
			}
			if (StringUtils.hasText(eventRuleVo.getSpecialDepartmentId())) {
				Department specialDepartment = departmentService.findById(eventRuleVo.getSpecialDepartmentId());
				eventRuleVo.setSpecialDepartment(specialDepartment);
			}
			if (StringUtils.hasText(eventRule.getNodeTypeId())) {
				eventRuleVo.setNodeType(nodeTypeService.findById(eventRule.getNodeTypeId()));
			}
		}
		return eventRuleVo;
	}
}
