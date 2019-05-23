package com.pepper.controller.emap.front.department;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String departmentId,String name) {
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
		systemLogService.log("get department group list", this.request.getRequestURL().toString());
		return pager;
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
