package com.pepper.controller.emap.front.staff;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.common.emuns.Gender;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.ExcelColumn;
import com.pepper.controller.emap.util.ExportExcelUtil;
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.event.HelpList;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.staff.Staff;
import com.pepper.model.emap.vo.HelpListVo;
import com.pepper.model.emap.vo.StaffVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.site.SiteInfoService;
import com.pepper.service.emap.staff.StaffService;
import com.pepper.service.file.FileService;
import com.pepper.util.MapToBeanUtil;

@Controller()
@RequestMapping(value = "/front/staff")
public class StaffController extends BaseControllerImpl implements BaseController {
	
	@Reference
	private StaffService staffService;
	
	@Reference
	private FileService fileService;
	
	@Reference
	private DepartmentService departmentService;
	
	@Reference
	private SiteInfoService siteInfoService;
	
	@Reference
	private SystemLogService systemLogService;
	@RequestMapping(value = "/export")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public void export(String name,String email,String siteId,String idCard,String status,String staffType,String userNo,String sex,String keyWord) throws IOException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		systemLogService.log("staff export", this.request.getRequestURL().toString());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xlsx");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("staff.xlsx", "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		Pager<Staff> pager = getPager(name, email, siteId, idCard,status,staffType,userNo,sex, keyWord, true);
		List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
		excelColumn.add(ExcelColumn.build("name", "name"));
		excelColumn.add(ExcelColumn.build("staffType", "staffType"));
		excelColumn.add(ExcelColumn.build("userNo", "userNo"));
		excelColumn.add(ExcelColumn.build("idCard", "idCard"));
		excelColumn.add(ExcelColumn.build("email", "email"));
		excelColumn.add(ExcelColumn.build("sex", "sex"));
		excelColumn.add(ExcelColumn.build("siteName", "site.name"));
		excelColumn.add(ExcelColumn.build("siteCode", "site.code"));
		excelColumn.add(ExcelColumn.build("status", "status"));
		new ExportExcelUtil().export((Collection<?>) pager.getData().get("staff"), outputStream, excelColumn);
	}
	
	private Pager<Staff> getPager(String name,String email,String siteId,String idCard,String status,String staffType,String userNo,String sex,String keyWord, Boolean isExport) {
		Pager<Staff> pager = new Pager<Staff>();
		if (Objects.equals(isExport, true)) {
			pager.setPageNo(1);
			pager.setPageSize(Integer.MAX_VALUE);
		}
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
		}
		if(StringUtils.hasText(siteId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_siteId",siteId );
		}
		if(StringUtils.hasText(email)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_email",email );
		}
		if(StringUtils.hasText(idCard)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_idCard",idCard );
		}
		if(StringUtils.hasText(status)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_status",status );
		}
		if(StringUtils.hasText(staffType)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_staffType",staffType );
		}
		if(StringUtils.hasText(userNo)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_userNo",userNo );
		}
		if(StringUtils.hasText(sex)) {
			if(Objects.equals(sex, Gender.MALE.getName())) {
				pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_sex",Gender.MALE.getKey() );
			}else if(Objects.equals(sex, Gender.FEMALE.getName())) {
				pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_sex",Gender.FEMALE.getKey() );
			}
		}
		if(StringUtils.hasText(keyWord)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.OR_LIKE+"_name&email&idCard&status&staffType&userNo",keyWord );
		}
		pager = staffService.findNavigator(pager);
		List<Staff> list = pager.getResults();
		List<StaffVo> returnList = new ArrayList<StaffVo>();
		for(Staff staff : list) {
			returnList.add(convertStaffVo(staff));
		}
		
		pager.setData("staff",returnList);
		pager.setResults(null);
		return pager;
	}
	
	
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String name,String email,String siteId,String idCard,String status,String staffType,String userNo,String sex,String keyWord) {
		
//		systemLogService.log("get staff list", this.request.getRequestURL().toString());
		return getPager(name, email, siteId, idCard,status,staffType,userNo,sex, keyWord, false);
	}

	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Staff staff = new Staff();
		MapToBeanUtil.convert(staff, map);
		if(map.containsKey("sex")) {
			if(Objects.equals(map.get("sex").toString(), Gender.MALE.getName())) {
				staff.setSex(Gender.MALE);
			}else if(Objects.equals(map.get("sex").toString(), Gender.FEMALE.getName())) {
				staff.setSex(Gender.FEMALE);
			}
		}
		staff.setAvailableTime(new Date());
		if(StringUtils.hasText(staff.getIdCard())&&staffService.findByIdCard(staff.getIdCard()).size()>=1) {
			resultData.setCode(1200002);
			resultData.setMessage(Internationalization.getMessageInternationalization(1200002).replace("第{1}行，", "").replace("{2}", staff.getIdCard()));
			return resultData;
		}
		staffService.save(staff);
		systemLogService.log("staff add", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Staff staff = new Staff();
		MapToBeanUtil.convert(staff, map);
		if(map.containsKey("sex")) {
			if(Objects.equals(map.get("sex").toString(), Gender.MALE.getName())) {
				staff.setSex(Gender.MALE);
			}else if(Objects.equals(map.get("sex").toString(), Gender.FEMALE.getName())) {
				staff.setSex(Gender.FEMALE);
			}
		}
		if(StringUtils.hasText(staff.getIdCard())){
			List<Staff> list = staffService.findByIdCard(staff.getIdCard());
			for(Staff obj : list) {
				if(!obj.getId().equals(staff.getId())) {
					resultData.setCode(1200002);
					resultData.setMessage(Internationalization.getMessageInternationalization(1200002).replace("第{1}行，", "").replace("{2}", staff.getIdCard()));
					return resultData;
				}
			}
		}
		staffService.update(staff);
		systemLogService.log("staff update", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		resultData.setData("staff",convertStaffVo(staffService.findById(id)));
		systemLogService.log("get staff info", this.request.getRequestURL().toString());
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
				staffService.deleteById(id);
			}catch (Exception e) {
			}
		}
		systemLogService.log("staff delete", this.request.getRequestURL().toString());
		return resultData;
	}
	
	
	@SuppressWarnings("resource")
	@RequestMapping(value = "/import")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importStaff(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		List<Staff> list = new ArrayList<Staff>();
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
				resultData.setCode(1200001);
				resultData.setMessage(Internationalization.getMessageInternationalization(1200001) );
				return resultData;
			}
			for(int i = 1 ; i <= totalRowNum ; i++)
	        {
				Row row = sheet.getRow(i);
				Staff staff = new Staff();
				staff.setName(getCellValue(row.getCell(0)).toString());
				staff.setStaffType(getCellValue(row.getCell(1)).toString());
				staff.setUserNo(getCellValue(row.getCell(2)).toString());
				staff.setIdCard(getCellValue(row.getCell(3)).toString().replaceAll("(\\.(\\d*))", ""));
				staff.setEmail(getCellValue(row.getCell(4)).toString());
				if(Objects.equals(getCellValue(row.getCell(5)).toString(), Gender.MALE.getDesc())) {
					staff.setSex(Gender.MALE);
				}else {
					staff.setSex(Gender.FEMALE);
				}
//				if(StringUtils.hasText(staff.getIdCard())&&staffService.findByIdCard(staff.getIdCard()).size()>=1) {
//					resultData.setCode(1200002);
//					resultData.setMessage(Internationalization.getMessageInternationalization(1200002).replace("{1}", String.valueOf(i)).replace("{2}", staff.getIdCard()));
//					return resultData;
//				}
				
				String site = getCellValue(row.getCell(7)).toString().toLowerCase();
				if(!StringUtils.hasText(site)) {
					resultData.setCode(1200003);
					resultData.setMessage(Internationalization.getMessageInternationalization(1200003).replace("{1}", String.valueOf(i)));
					return resultData;
				}else {
					SiteInfo siteInfo = this.siteInfoService.findByCode(site);
					if(Objects.isNull(siteInfo)) {
						resultData.setCode(1200003);
						resultData.setMessage(Internationalization.getMessageInternationalization(1200003).replace("{1}", String.valueOf(i)));
						return resultData;
					}
					staff.setSiteId(siteInfo.getId());
				}
				if(!StringUtils.hasText(staff.getName())) {
					resultData.setCode(1200004);
					resultData.setMessage(Internationalization.getMessageInternationalization(1200004).replace("{1}", String.valueOf(i)));
					return resultData;
				}

				staff.setStatus(getCellValue(row.getCell(8)).toString());


				if (StringUtils.hasText(staff.getIdCard())) {
					List<Staff> listStaff = staffService.findByIdCard(staff.getIdCard());
					Staff oldStaff = listStaff.size()>0?listStaff.get(0):null;
					if(Objects.nonNull(oldStaff)) {
						String isDelete = getCellValue(row.getCell(9)).toString();
						if(Objects.equals(isDelete.trim(), "是")) {
							staffService.deleteById(oldStaff.getId());
							continue;
						}else {
							staff.setId(oldStaff.getId());
							staffService.update(staff);
							continue;
						}
					}
					list.add(staff);
				}
				
				list.add(staff);
	        }
			
			this.staffService.saveAll(list);
		}
//		systemLogService.log("staff import");
		return resultData;
	}
	
	private  boolean isExcel2003(String filePath){
        return StringUtils.hasText(filePath) && filePath.endsWith(".xls");
    }
	private  boolean isExcel2007(String filePath){
        return StringUtils.hasText(filePath) && filePath.endsWith(".xlsx");
    }
	
	private Boolean check(Row row) {
		if(!getCellValue(row.getCell(0)).toString().equals("name")) {
			return false;
		}
		if(!getCellValue(row.getCell(1)).toString().equals("staffType")) {
			return false;
		}
		if(!getCellValue(row.getCell(2)).toString().equals("userNo")) {
			return false;
		}
		if(!getCellValue(row.getCell(3)).toString().equals("idCard")) {
			return false;
		}
		if(!getCellValue(row.getCell(4)).toString().equals("email")) {
			return false;
		}
		if(!getCellValue(row.getCell(5)).toString().equals("sex")) {
			return false;
		}
		if(!getCellValue(row.getCell(7)).toString().equals("siteCode")) {
			return false;
		}
		if(!getCellValue(row.getCell(8)).toString().equals("status")) {
			return false;
		}
		if(!getCellValue(row.getCell(9)).toString().equals("isDelete")) {
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
	
	
	
	private StaffVo convertStaffVo(Staff staff) {
		StaffVo staffVo = new StaffVo();
		BeanUtils.copyProperties(staff, staffVo);
		staffVo.setSite(siteInfoService.findById(staff.getSiteId()));
		return staffVo;
	}
}
