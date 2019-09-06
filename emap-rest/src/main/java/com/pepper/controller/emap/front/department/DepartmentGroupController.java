package com.pepper.controller.emap.front.department;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.pepper.model.emap.building.BuildingInfo;
import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.department.DepartmentGroup;
import com.pepper.model.emap.vo.DepartmentGroupVo;
import com.pepper.model.emap.vo.DepartmentVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.department.DepartmentGroupService;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.util.MapToBeanUtil;

@Controller()
@RequestMapping(value = "/front/departmentGroup")
public class DepartmentGroupController extends BaseControllerImpl implements BaseController {

	@Reference
	private DepartmentService departmentService;
	
	@Reference
	private DepartmentGroupService departmentGroupService;
	
	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping(value = "/export")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public void export(String departmentId,String name) throws IOException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		systemLogService.log("departmentGroup export", this.request.getRequestURL().toString());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xlsx");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("departmentGroup.xlsx", "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		Pager<DepartmentGroup> pager = getPager(departmentId, name, true);
		List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
		excelColumn.add(ExcelColumn.build("部門", "department.name"));
		excelColumn.add(ExcelColumn.build("名稱", "name"));
		excelColumn.add(ExcelColumn.build("開始時間", "startTime"));
		excelColumn.add(ExcelColumn.build("結束時間", "endTime"));
		new ExportExcelUtil().export((Collection<?>) pager.getData().get("departmentGroup"), outputStream, excelColumn);
	}
	
	private Pager<DepartmentGroup> getPager(String departmentId,String name, Boolean isExport) {
		Pager<DepartmentGroup> pager = new Pager<DepartmentGroup>();
		if(StringUtils.hasText(departmentId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_departmentId",departmentId );
		}
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
		}
		
		pager = departmentGroupService.findNavigator(pager);
		List<DepartmentGroup> list = pager.getResults();
		List<DepartmentGroupVo> returnList = new ArrayList<DepartmentGroupVo>();
		for(DepartmentGroup departmentGroup : list) {
			DepartmentGroupVo departmentGroupVo = new DepartmentGroupVo();
			BeanUtils.copyProperties(departmentGroup, departmentGroupVo);
			departmentGroupVo.setDepartment(departmentService.findById(departmentGroup.getDepartmentId()));
			returnList.add(departmentGroupVo);
		}
		
		pager.setData("departmentGroup",returnList);
		pager.setResults(null);
		return pager;
	}
	
	@RequestMapping(value = "/import")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importStaff(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		List<DepartmentGroup> list = new ArrayList<DepartmentGroup>();
		for (String fileName : files.keySet()) {
			MultipartFile file = files.get(fileName);
			Workbook wookbook = null;
	        try {
	        	if(isExcel2003(fileName)){
	        		wookbook = new HSSFWorkbook(file.getInputStream());
	        	}else if(isExcel2007(fileName)){
	        		wookbook = new XSSFWorkbook(file.getInputStream());
	        	}
	        } catch (IOException e) {
	        }
	        
	        Sheet sheet = wookbook.getSheetAt(0);
	        Row rowHead = sheet.getRow(0);
			int totalRowNum = sheet.getLastRowNum();
			if(!check(sheet.getRow(0))) {
				resultData.setMessage("数据错误！");
				return resultData;
			}
			for(int i = 1 ; i <= totalRowNum ; i++)
	        {
				Row row = sheet.getRow(i);
				DepartmentGroup departmentGroup = new DepartmentGroup();
				String department = getCellValue(row.getCell(0)).toString();
				departmentGroup.setName(getCellValue(row.getCell(1)).toString());
				departmentGroup.setStartTime(getCellValue(row.getCell(2)).toString());
				departmentGroup.setEndTime(getCellValue(row.getCell(3)).toString());
				if (StringUtils.hasText(department)&&departmentService.findDepartment(department) != null) {
					departmentGroup.setDepartmentId(departmentService.findDepartment(department).getId());
					list.add(departmentGroup);
				}
	        }
			this.departmentGroupService.saveAll(list);
		}
		systemLogService.log("import departmentGroup", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private  boolean isExcel2003(String filePath){
        return StringUtils.hasText(filePath) && filePath.endsWith(".xls");
    }
	private  boolean isExcel2007(String filePath){
        return StringUtils.hasText(filePath) && filePath.endsWith(".xlsx");
    }
	
	private Boolean check(Row row) {
		if(!getCellValue(row.getCell(0)).toString().equals("department")) {
			return false;
		}
		if(!getCellValue(row.getCell(1)).toString().equals("name")) {
			return false;
		}
		if(!getCellValue(row.getCell(2)).toString().equals("startTime")) {
			return false;
		}
		if(!getCellValue(row.getCell(3)).toString().equals("endTime")) {
			return false;
		}
		return true;
	}
	
	private Object getCellValue(Cell cell) {
		if(cell == null) {
			return "";
		}
		Object object = "";
		switch (cell.getCellType()) {
		case STRING :
			object = cell.getStringCellValue();
			break;
		case NUMERIC :
			object = cell.getNumericCellValue();
			break;
		case BOOLEAN :
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
	public Object list(String departmentId,String name) {
		
		systemLogService.log("get department group list", this.request.getRequestURL().toString());
		return getPager(departmentId, name, false);
	}
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) throws IOException {
		ResultData resultData = new ResultData();
		DepartmentGroup departmentGroup = new DepartmentGroup();
		MapToBeanUtil.convert(departmentGroup, map);
		departmentGroupService.save(departmentGroup);
		systemLogService.log("department group add", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) throws IOException {
		ResultData resultData = new ResultData();
		DepartmentGroup departmentGroup = new DepartmentGroup();
		MapToBeanUtil.convert(departmentGroup, map);
		departmentGroupService.update(departmentGroup);
		systemLogService.log("department group update", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		DepartmentGroup departmentGroup = departmentGroupService.findById(id);
		DepartmentGroupVo departmentGroupVo = new DepartmentGroupVo();
		BeanUtils.copyProperties(departmentGroup, departmentGroupVo);
		departmentGroupVo.setDepartment(this.departmentService.findById(departmentGroup.getDepartmentId()));
		resultData.setData("departmentGroup",departmentGroupVo);
		systemLogService.log("get department group info", this.request.getRequestURL().toString());
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
				departmentGroupService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("department group delete", this.request.getRequestURL().toString());
		return resultData;
	}
}
