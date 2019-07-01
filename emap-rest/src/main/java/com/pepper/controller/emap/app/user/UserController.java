package com.pepper.controller.emap.app.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.zookeeper.server.admin.AdminServer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.console.enums.UserType;
import com.pepper.model.console.role.Role;
import com.pepper.model.console.role.RoleUser;
import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.department.DepartmentGroup;
import com.pepper.model.emap.event.EventListAssist;
import com.pepper.model.emap.vo.AdminUserEventAssistVo;
import com.pepper.model.emap.vo.AdminUserVo;
import com.pepper.model.emap.vo.DepartmentGroupVo1;
import com.pepper.model.emap.vo.DepartmentGroupVo2;
import com.pepper.model.emap.vo.DepartmentVo1;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.console.role.RoleService;
import com.pepper.service.console.role.RoleUserService;
import com.pepper.service.emap.department.DepartmentGroupService;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.file.FileService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;

@Controller("appUserController")
@RequestMapping(value = "/app/user")
@Validated
public class UserController extends BaseControllerImpl implements BaseController {
	@Reference
	private RoleUserService roleUserService;
	
	@Reference
	private RoleService roleService;
	
	@Reference
	private DepartmentService departmentService;
	
	@Reference
	private FileService fileService;
	
	@Reference
	private AdminUserService adminUserService;
	
	@Reference
	private ValueOperationsService valueOperationsService;
	
	@Reference
	private SystemLogService systemLogService;
	
	@Reference
	private DepartmentGroupService departmentGroupService;
	
	@Reference
	private com.pepper.service.emap.event.EventListAssistService eventListAssistService;
	
	@RequestMapping(value = "/getUserInfo")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object getUserInfo() {
		ResultData resultData = new ResultData();
		AdminUser adminUser = (AdminUser) this.getCurrentUser();
		if(adminUser!=null) {
			adminUser = this.adminUserService.findById(adminUser.getId());
		}else {
			return resultData;
		}
		adminUser.setPassword("");
		AdminUserVo  adminUserVo = new AdminUserVo();
		BeanUtils.copyProperties(adminUser, adminUserVo);
		adminUserVo.setPassword("");
		RoleUser roleUser = roleUserService.findByUserId(adminUser.getId());
		adminUserVo.setRole(roleService.findById(roleUser.getRoleId()));
		if(StringUtils.hasText(adminUser.getDepartmentId())) {
			adminUserVo.setDepartment(departmentService.findById(adminUser.getDepartmentId()));
		}
		adminUserVo.setHeadPortraitUrl(fileService.getUrl(adminUser.getHeadPortrait()));
		resultData.setData("user", adminUserVo);
		
		systemLogService.log("app get user info", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/work")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object work(@RequestBody java.util.Map<String,Object> map) {
		ResultData resultData = new ResultData();
		AdminUser adminUser = (AdminUser) this.getCurrentUser();
		adminUser = adminUserService.findById(adminUser.getId());
		if(map.containsKey("work")) {
			adminUser.setIsWork(Boolean.valueOf(map.get("work").toString()));
			adminUserService.update(adminUser);
		}
		
		systemLogService.log("app user work", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/department/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object departmentList() {
		AdminUser currentUser = (AdminUser) this.getCurrentUser();
		ResultData resultData = new ResultData();
		if(!StringUtils.hasText(currentUser.getDepartmentId())) {
			return resultData;
		}
		Department department =  departmentService.findById(currentUser.getDepartmentId());
		if(department==null) {
			return resultData;
		}		
		resultData.setData("user", convertDepartmentVo1(department));
		systemLogService.log("app get department user list", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private DepartmentVo1 convertDepartmentVo1(Department department) {
		AdminUser currentUser = (AdminUser) this.getCurrentUser();
		DepartmentVo1 departmentVo1 = new DepartmentVo1();
		BeanUtils.copyProperties(department, departmentVo1);
		List<DepartmentGroup> listDepartmentGroup = departmentGroupService.findByDepartmentId(department.getId());
		List<DepartmentGroupVo1> listDepartmentGroupV1 = new ArrayList<DepartmentGroupVo1>();
		for(DepartmentGroup departmentGroup : listDepartmentGroup) {
			DepartmentGroupVo1 departmentGroupV1 = new DepartmentGroupVo1();
			BeanUtils.copyProperties(departmentGroup, departmentGroupV1);
			List<AdminUser> listAdminUser = this.adminUserService.findByDepartmentGroupIdAndIdNot(departmentGroup.getId(),currentUser.getId());
			List<AdminUserVo> listAdminUserVo = new ArrayList<AdminUserVo>();
			for(AdminUser adminUser : listAdminUser) {
				AdminUserVo adminUserVo = new AdminUserVo();
				BeanUtils.copyProperties(adminUser, adminUserVo);
				adminUserVo.setHeadPortraitUrl(this.fileService.getUrl(adminUser.getHeadPortrait()));
				listAdminUserVo.add(adminUserVo);
			}
			departmentGroupV1.setUser(listAdminUserVo);
			listDepartmentGroupV1.add(departmentGroupV1);
		}
		departmentVo1.setDepartmentGroup(listDepartmentGroupV1);
		return departmentVo1;
	}
	
	@RequestMapping(value = "/bindDeviceId")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object bindDeviceId(@RequestBody Map<String,String> map) {
		AdminUser currentUser = (AdminUser) this.getCurrentUser();
		valueOperationsService.set("userDeviceId_"+currentUser.getId(), map.get("deviceId"));
		
		systemLogService.log("app bind device id", this.request.getRequestURL().toString());
		return new ResultData();
	}
	
	@RequestMapping(value = "/department/user")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object departmentUser(String eventListId) {
		AdminUser currentUser = (AdminUser) this.getCurrentUser();
		ResultData resultData = new ResultData();
		if(!StringUtils.hasText(currentUser.getDepartmentId())) {
			return resultData;
		}
		Department department =  departmentService.findById(currentUser.getDepartmentId());
		if(department==null) {
			return resultData;
		}
		
		
		DepartmentVo1 departmentVo1 = convertDepartmentVo1(department);
		for(DepartmentGroupVo1 departmentGroupVo1  : departmentVo1.getDepartmentGroup()) {
			for(AdminUserVo adminUser : departmentGroupVo1.getUser()) {
				adminUser.setIsRequestAssist(this.eventListAssistService.findEventListAssist(eventListId, adminUser.getId(),currentUser.getId(),null)==null?false:true);
			}
		}
		resultData.setData("user", departmentVo1);
		systemLogService.log("app get department user list", this.request.getRequestURL().toString());
		return resultData;
	}
	
}
