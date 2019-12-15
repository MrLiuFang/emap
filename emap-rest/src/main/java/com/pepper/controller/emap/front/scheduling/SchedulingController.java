package com.pepper.controller.emap.front.scheduling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
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
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.department.DepartmentGroup;
import com.pepper.model.emap.scheduling.Scheduling;
import com.pepper.model.emap.vo.SchedulingVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.emap.department.DepartmentGroupService;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.scheduling.SchedulingService;

@Controller()
@RequestMapping(value = "/front/scheduling")
@Validated
public class SchedulingController extends BaseControllerImpl implements BaseController {
	
	@Reference
	private SchedulingService schedulingService;
	
	@Reference
	private AdminUserService adminUserService;
	
	@Reference
	private DepartmentService departmentService;
	
	@Reference
	private DepartmentGroupService departmentGroupService;
	
	@Reference
	private SystemLogService systemLogService;
	
	private Pager<Scheduling> getPager(String userId, String userName, String departmentId, String departmentName, String departmentGroupId, String departmentGroupName,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
			Boolean isExport) {
		Pager<Scheduling> pager = new Pager<Scheduling>();
		if (Objects.equals(isExport, true)) {
			pager.setPageNo(1);
			pager.setPageSize(Integer.MAX_VALUE);
		}
		pager = schedulingService.findNavigator(pager, userId, userName, departmentId, departmentName, departmentGroupId, departmentGroupName, startDate, endDate);

		List<Scheduling> list = pager.getResults();
		List<SchedulingVo> returnList = new ArrayList<SchedulingVo>();
		for (Scheduling scheduling : list) {
			returnList.add(convertSchedulingVo(scheduling));
		}
		pager.setData("scheduling", returnList);
		pager.setResults(null);
		return pager;
	}

	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String userId, String userName, String departmentId, String departmentName, String departmentGroupId, String departmentGroupName,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,Boolean isMe) {
		if (Objects.nonNull(isMe)){
			AdminUser user = (AdminUser) this.getCurrentUser();
			userId = user.getId();
		}
		return getPager(userId, userName, departmentId, departmentName, departmentGroupId, departmentGroupName, startDate, endDate, false);
	}

	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody List<Scheduling> scheduling) {
		ResultData resultData = new ResultData();
		schedulingService.saveAll(scheduling);
		systemLogService.log("scheduling add", this.request.getRequestURL().toString());
		return resultData;
	}

	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody List<Scheduling> list,String[] userId,@DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
		ResultData resultData = new ResultData();
		if(Objects.nonNull(userId)&&Objects.nonNull(startDate)&&Objects.nonNull(endDate)) {
			for(String id : userId){
				schedulingService.deleteByUserIdAndDateBetween(id, startDate, endDate);
			}
		}
		for(Scheduling scheduling : list) {
			schedulingService.deleteByUserIdAndDate(scheduling.getUserId(), scheduling.getDate());
		}
		schedulingService.saveAll(list);
		systemLogService.log("scheduling update", this.request.getRequestURL().toString());
		return resultData;
	}

	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit( Scheduling scheduling) {
		ResultData resultData = new ResultData();
		Example<Scheduling> example =Example.of(scheduling);
		Sort sort = new Sort(Direction.DESC, "userId","date","departmentId","departmentGroupId");
		List<Scheduling> list = this.schedulingService.findAll(example,sort);
		List<SchedulingVo> returnList = new ArrayList<SchedulingVo>();
		for (Scheduling obj : list) {
			returnList.add(convertSchedulingVo(obj));
		}
		resultData.setData("scheduling", returnList);
		systemLogService.log("get node info", this.request.getRequestURL().toString());
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
				this.schedulingService.deleteById(id);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("node delete", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private SchedulingVo  convertSchedulingVo(Scheduling scheduling) {
		SchedulingVo schedulingVo = new SchedulingVo();
		BeanUtils.copyProperties(scheduling, 	schedulingVo);
		AdminUser user = this.adminUserService.findById(schedulingVo.getUserId());
		if(Objects.nonNull(user)) {
			schedulingVo.setUserName(user.getName());
		}
		Department department = this.departmentService.findById(schedulingVo.getDepartmentId());
		if(Objects.nonNull(department)) {
			schedulingVo.setDepartmentName(department.getName());
		}
		DepartmentGroup departmentGroup = this.departmentGroupService.findById(schedulingVo.getDepartmentGroupId());
		if(Objects.nonNull(departmentGroup)) {
			schedulingVo.setDepartmentGroupName(departmentGroup.getName());
		}
		return schedulingVo;
	}
}
