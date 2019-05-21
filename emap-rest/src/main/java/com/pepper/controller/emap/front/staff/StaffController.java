package com.pepper.controller.emap.front.staff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import com.pepper.common.emuns.Status;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.console.enums.UserType;
import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.staff.Staff;
import com.pepper.model.emap.vo.NodeTypeVo;
import com.pepper.model.emap.vo.StaffVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.service.emap.site.SiteInfoService;
import com.pepper.service.emap.staff.StaffService;
import com.pepper.service.file.FileService;
import com.pepper.util.MapToBeanUtil;
import com.pepper.util.Md5Util;

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
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		resultData.setData("staff",convertStaffVo(staffService.findById(id)));
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
		return resultData;
	}
	
	
	
	private StaffVo convertStaffVo(Staff staff) {
		StaffVo staffVo = new StaffVo();
		BeanUtils.copyProperties(staff, staffVo);
		staffVo.setSite(siteInfoService.findById(staff.getSiteId()));
		return staffVo;
	}
}
