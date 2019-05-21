package com.pepper.controller.emap.front.department;

import java.io.IOException;
import java.util.Iterator;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.department.DepartmentGroup;
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
		systemLogService.log("get department list", this.request.getRequestURL().toString());
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
		department.setCode(jsonNode.get("code").asText(""));
		department.setName(jsonNode.get("name").asText(""));
		department.setRemark(jsonNode.get("remark").asText(""));
		if(departmentService.findByCode(department.getCode())!=null) {
			resultData.setCode(2000001);
			resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
			return resultData;
		}
		department = departmentService.save(department);
		
		if(jsonNode.has("group") && jsonNode.get("group").isArray()){
			Iterator<JsonNode> group = jsonNode.get("group").iterator();
			while(group.hasNext()){
				JsonNode departmentGroupJsonNode = group.next();
				DepartmentGroup departmentGroup = new DepartmentGroup();
				departmentGroup.setDepartmentId(department.getId());
				departmentGroup.setEndTime(departmentGroupJsonNode.get("endTime").asText(""));
				departmentGroup.setStartTime(departmentGroupJsonNode.get("startTime").asText(""));
				departmentGroup.setName(departmentGroupJsonNode.get("name").asText(""));
				departmentGroupService.save(departmentGroup);
			}
		}
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
		department.setCode(jsonNode.get("code").asText(""));
		department.setName(jsonNode.get("name").asText(""));
		department.setRemark(jsonNode.get("remark").asText(""));
		department.setId(jsonNode.get("id").asText(""));
		Department oldDepartment = departmentService.findById(department.getId());
		if(!department.getCode().equals(oldDepartment.getCode())) {
			if(departmentService.findByCode(department.getCode())!=null) {
				resultData.setCode(2000001);
				resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
				return resultData;
			}
		}
		departmentService.update(department);
		departmentGroupService.deleteByDepartmentId(department.getId());
		if(jsonNode.has("group") && jsonNode.get("group").isArray()){
			Iterator<JsonNode> group = jsonNode.get("group").iterator();
			while(group.hasNext()){
				JsonNode departmentGroupJsonNode = group.next();
				DepartmentGroup departmentGroup = new DepartmentGroup();
				departmentGroup.setDepartmentId(department.getId());
				departmentGroup.setEndTime(departmentGroupJsonNode.get("endTime").asText(""));
				departmentGroup.setStartTime(departmentGroupJsonNode.get("startTime").asText(""));
				departmentGroup.setName(departmentGroupJsonNode.get("name").asText(""));
				departmentGroupService.save(departmentGroup);
			}
		}
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
