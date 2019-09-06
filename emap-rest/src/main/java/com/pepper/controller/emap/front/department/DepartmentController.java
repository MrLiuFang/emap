package com.pepper.controller.emap.front.department;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
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
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.vo.DepartmentVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.department.DepartmentGroupService;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.service.emap.log.SystemLogService;

@Controller()
@RequestMapping(value = "/front/department")
public class DepartmentController  extends BaseControllerImpl implements BaseController  {

	@Reference
	private DepartmentService departmentService;
	
	@Reference
	private DepartmentGroupService departmentGroupService;
	
	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping(value = "/export")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public void export(String name,String code,String keyWord) throws IOException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		systemLogService.log("department export", this.request.getRequestURL().toString());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xlsx");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("department.xlsx", "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		Pager<Department> pager = getPager(code, name, keyWord, true);
		List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
		excelColumn.add(ExcelColumn.build("編碼", "code"));
		excelColumn.add(ExcelColumn.build("名稱", "name"));
		new ExportExcelUtil().export((Collection<?>) pager.getData().get("department"), outputStream, excelColumn);
	}
	
	@RequestMapping(value = "/import")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importStaff(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		List<Department> list = new ArrayList<Department>();
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
				Department department = new Department();
				department.setCode(getCellValue(row.getCell(0)).toString());
				department.setName(getCellValue(row.getCell(1)).toString());
				
				if (StringUtils.hasText(department.getCode())&&departmentService.findByCode(department.getCode()) == null) {
					list.add(department);
				}
	        }
			this.departmentService.saveAll(list);
		}
		systemLogService.log("import department", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private  boolean isExcel2003(String filePath){
        return StringUtils.hasText(filePath) && filePath.endsWith(".xls");
    }
	private  boolean isExcel2007(String filePath){
        return StringUtils.hasText(filePath) && filePath.endsWith(".xlsx");
    }
	
	private Boolean check(Row row) {
		if(!getCellValue(row.getCell(0)).toString().equals("code")) {
			return false;
		}
		if(!getCellValue(row.getCell(1)).toString().equals("name")) {
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
	public Object list(String name,String code,String keyWord) {
		
		systemLogService.log("get department list", this.request.getRequestURL().toString());
		return getPager(name, code, keyWord, false);
	}
	
	private Pager<Department> getPager(String name,String code,String keyWord, Boolean isExport) {
		Pager<Department> pager = new Pager<Department>();
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
		}
		if(StringUtils.hasText(code)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_code",code );
		}
		
		if(StringUtils.hasText(keyWord)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.OR_LIKE+"_code&name",keyWord );
		}
		pager = departmentService.findNavigator(pager);
		pager.setData("department",pager.getResults());
		pager.setResults(null);
		return pager;
	}

	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody String data) throws IOException {
		ResultData resultData = new ResultData();
		Department department = new Department();
//		MapToBeanUtil.convert(department, map);

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(data);
		if(jsonNode.has("code")) {
			department.setCode(jsonNode.get("code").asText(""));
		}
		if(jsonNode.has("name")) {
			department.setName(jsonNode.get("name").asText(""));
		}
		if(jsonNode.has("remark")) {
			department.setRemark(jsonNode.get("remark").asText(""));
		}
		if(departmentService.findByCode(department.getCode())!=null) {
			resultData.setCode(2000001);
			resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
			return resultData;
		}
		department = departmentService.save(department);
		
//		if(jsonNode.has("group") && jsonNode.get("group").isArray()){
//			Iterator<JsonNode> group = jsonNode.get("group").iterator();
//			while(group.hasNext()){
//				JsonNode departmentGroupJsonNode = group.next();
//				DepartmentGroup departmentGroup = new DepartmentGroup();
//				departmentGroup.setDepartmentId(department.getId());
//				if(departmentGroupJsonNode.has("endTime")) {
//					departmentGroup.setEndTime(departmentGroupJsonNode.get("endTime").asText(""));
//				}
//				if(departmentGroupJsonNode.has("startTime")) {
//					departmentGroup.setStartTime(departmentGroupJsonNode.get("startTime").asText(""));
//				}
//				if(departmentGroupJsonNode.has("name")) {
//					departmentGroup.setName(departmentGroupJsonNode.get("name").asText(""));
//				}
//				departmentGroupService.save(departmentGroup);
//			}
//		}
		systemLogService.log("department add", this.request.getRequestURL().toString());
		return resultData;
	}
	
	
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody String data) throws IOException {
		ResultData resultData = new ResultData();
		Department department = new Department();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(data);
		if(jsonNode.has("code")) {
			department.setCode(jsonNode.get("code").asText(""));
		}
		if(jsonNode.has("name")) {
			department.setName(jsonNode.get("name").asText(""));
		}
		if(jsonNode.has("remark")) {
			department.setRemark(jsonNode.get("remark").asText(""));
		}
		department.setId(jsonNode.get("id").asText(""));
		
		Department oldDepartment = departmentService.findByCode(department.getCode());
		if(oldDepartment!=null && oldDepartment.getCode()!=null&&department.getCode()!=null) {
			if(!department.getId().equals(oldDepartment.getId())){
				resultData.setCode(2000001);
				resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
				return resultData;
			}
		}
		
		departmentService.update(department);
//		departmentGroupService.deleteByDepartmentId(department.getId());
//		if(jsonNode.has("group") && jsonNode.get("group").isArray()){
//			Iterator<JsonNode> group = jsonNode.get("group").iterator();
//			while(group.hasNext()){
//				JsonNode departmentGroupJsonNode = group.next();
//				DepartmentGroup departmentGroup = new DepartmentGroup();
//				departmentGroup.setDepartmentId(department.getId());
//				if(departmentGroupJsonNode.has("endTime")) {
//					departmentGroup.setEndTime(departmentGroupJsonNode.get("endTime").asText(""));
//				}
//				if(departmentGroupJsonNode.has("startTime")) {
//					departmentGroup.setStartTime(departmentGroupJsonNode.get("startTime").asText(""));
//				}
//				if(departmentGroupJsonNode.has("name")) {
//					departmentGroup.setName(departmentGroupJsonNode.get("name").asText(""));
//				}
//				departmentGroupService.save(departmentGroup);
//			}
//		}
		systemLogService.log("department update", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		Department department = departmentService.findById(id);
		DepartmentVo departmentVo = new DepartmentVo();
		BeanUtils.copyProperties(department, departmentVo);
		departmentVo.setDepartmentGroup(departmentGroupService.findByDepartmentId(department.getId()));
		resultData.setData("department",departmentVo);
		systemLogService.log("get department info", this.request.getRequestURL().toString());
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
				departmentService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("department delete", this.request.getRequestURL().toString());
		return resultData;
	}
	
//	private StaffVo convertStaffVo(Staff staff) {
//		StaffVo staffVo = new StaffVo();
//		BeanUtils.copyProperties(staff, staffVo);
//		staffVo.setDepartment(departmentService.findById(staff.getDepartmentId()));
//		return staffVo;
//	}
}
