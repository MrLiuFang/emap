package com.pepper.controller.emap.front.department;

import java.io.IOException;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.department.Department;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.util.MapToBeanUtil;

@Controller()
@RequestMapping(value = "/front/department")
public class DepartmentController  extends BaseControllerImpl implements BaseController  {

	@Reference
	private DepartmentService departmentService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String name,String code,String keyWord) {
		Pager<Department> pager = new Pager<Department>();
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
		}
		if(StringUtils.hasText(code)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_code",code );
		}
		
		if(StringUtils.hasText(keyWord)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.ORLIKE+"_code&name",keyWord );
		}
		pager = departmentService.findNavigator(pager);
		pager.setData("department",pager.getResults());
		pager.setResults(null);
		return pager;
	}

	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Department department = new Department();
		MapToBeanUtil.convert(department, map);
		departmentService.save(department);
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Department department = new Department();
		MapToBeanUtil.convert(department, map);
		departmentService.update(department);
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		resultData.setData("department",departmentService.findById(id));
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
		return resultData;
	}
	
//	private StaffVo convertStaffVo(Staff staff) {
//		StaffVo staffVo = new StaffVo();
//		BeanUtils.copyProperties(staff, staffVo);
//		staffVo.setDepartment(departmentService.findById(staff.getDepartmentId()));
//		return staffVo;
//	}
}
