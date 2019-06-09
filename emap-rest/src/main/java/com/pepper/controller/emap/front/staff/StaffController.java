package com.pepper.controller.emap.front.staff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
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
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.staff.Staff;
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
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String name,String email,String siteId) {
		Pager<Staff> pager = new Pager<Staff>();
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
		}
		if(StringUtils.hasText(siteId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_siteId",siteId );
		}
		if(StringUtils.hasText(email)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_email",email );
		}
		pager = staffService.findNavigator(pager);
		List<Staff> list = pager.getResults();
		List<StaffVo> returnList = new ArrayList<StaffVo>();
		for(Staff staff : list) {
			returnList.add(convertStaffVo(staff));
		}
		
		pager.setData("staff",returnList);
		pager.setResults(null);
		systemLogService.log("get staff list", this.request.getRequestURL().toString());
		return pager;
	}

	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Staff staff = new Staff();
		MapToBeanUtil.convert(staff, map);
		staff.setAvailableTime(new Date());
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
			Workbook wookbook = new XSSFWorkbook(file.getInputStream());
	        Sheet sheet = wookbook.getSheetAt(0);
	        Row rowHead = sheet.getRow(0);
			int totalRowNum = sheet.getLastRowNum();
			if(!check(sheet.getRow(0))) {
				resultData.setCode(4000003);
				resultData.setMessage("数据错误！（非staff结构数据）");
				return resultData;
			}
			for(int i = 1 ; i <= totalRowNum ; i++)
	        {
				Row row = sheet.getRow(i);
				Staff staff = new Staff();
				staff.setName(getCellValue(row.getCell(0)).toString());
				staff.setEmail(getCellValue(row.getCell(1)).toString());
				staff.setPassword(getCellValue(row.getCell(2)).toString());
				staff.setIdCard(getCellValue(row.getCell(3)).toString());
				if(staffService.findByIdCard(staff.getIdCard())!=null) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，"+staff.getIdCard()+"已存在");
					return resultData;
				}
				String needChangePassword = getCellValue(row.getCell(4)).toString().toLowerCase();
				if(StringUtils.hasText(needChangePassword)&&(needChangePassword.equals("true")||needChangePassword.equals("false"))) {
					staff.setNeedChangePassword(Boolean.valueOf(needChangePassword));
				}else {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，needChangePassword数据错误");
					return resultData;
				}
				
				String passwordNeverExpire = getCellValue(row.getCell(5)).toString().toLowerCase();
				if(StringUtils.hasText(passwordNeverExpire)&&(passwordNeverExpire.equals("true")||needChangePassword.equals("false"))) {
					staff.setNeedChangePassword(Boolean.valueOf(passwordNeverExpire));
				}else {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，passwordNeverExpire数据错误");
					return resultData;
				}
				
				String isAvailable = getCellValue(row.getCell(6)).toString().toLowerCase();
				if(StringUtils.hasText(isAvailable)&&(isAvailable.equals("true")||isAvailable.equals("false"))) {
					staff.setNeedChangePassword(Boolean.valueOf(isAvailable));
				}else {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，passwordNeverExpire数据错误");
					return resultData;
				}
				
				String site = getCellValue(row.getCell(7)).toString().toLowerCase();
				List<SiteInfo> listSiteInfo = this.siteInfoService.findByName(site);
				if(listSiteInfo.size()!=1) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，site数据错误(空值/找到多个site)");
					return resultData;
				}
				
				staff.setSiteId(listSiteInfo.get(0).getId());
				
				if(!StringUtils.hasText(staff.getName())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，name不能为空");
					return resultData;
				}
				
				if(!StringUtils.hasText(staff.getEmail())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，email不能为空");
					return resultData;
				}
				
				if(!StringUtils.hasText(staff.getPassword())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，password不能为空");
					return resultData;
				}
				
				if(!StringUtils.hasText(staff.getIdCard())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，idCard不能为空");
					return resultData;
				}
				
				list.add(staff);
	        }
			
			this.staffService.saveAll(list);
		}
		systemLogService.log("staff import", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private Boolean check(Row row) {
		if(!getCellValue(row.getCell(0)).toString().equals("name")) {
			return false;
		}
		if(!getCellValue(row.getCell(1)).toString().equals("email")) {
			return false;
		}
		if(!getCellValue(row.getCell(2)).toString().equals("password")) {
			return false;
		}
		if(!getCellValue(row.getCell(3)).toString().equals("idCard")) {
			return false;
		}
		if(!getCellValue(row.getCell(4)).toString().equals("needChangePassword")) {
			return false;
		}
		if(!getCellValue(row.getCell(5)).toString().equals("passwordNeverExpire")) {
			return false;
		}
		if(!getCellValue(row.getCell(6)).toString().equals("isAvailable")) {
			return false;
		}
		if(!getCellValue(row.getCell(7)).toString().equals("site")) {
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
