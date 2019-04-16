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
import com.pepper.model.emap.vo.AdminUserVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.console.role.RoleService;
import com.pepper.service.console.role.RoleUserService;
import com.pepper.service.emap.department.DepartmentService;
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
	
	@RequestMapping(value = "/getUserInfo")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object getUserInfo() {
		ResultData resultData = new ResultData();
		AdminUser adminUser = (AdminUser) this.getCurrentUser();
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
		return resultData;
	}
	
	@RequestMapping(value = "/getUserByBepartmentId")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object getUserByBepartmentId() {
		ResultData resultData = new ResultData();
		AdminUser currentUser = (AdminUser) this.getCurrentUser();
		currentUser = adminUserService.findById(currentUser.getId());
		List<AdminUser> list = adminUserService.findUserByBepartmentId(currentUser.getDepartmentId());
		List<AdminUserVo> returnList = new ArrayList<AdminUserVo>();
		for(AdminUser user : list) {
			AdminUserVo adminUserVo = new AdminUserVo();
			BeanUtils.copyProperties(user, adminUserVo);
			if(StringUtils.hasText(user.getHeadPortrait())) {
				adminUserVo.setHeadPortraitUrl(fileService.getUrl(user.getHeadPortrait()));
				adminUserVo.setPassword("");
				returnList.add(adminUserVo);
			}
		}
		resultData.setData("user", returnList);
		return resultData;
	}
	
	@RequestMapping(value = "/bindDeviceId")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object bindDeviceId(String deviceId) {
		AdminUser currentUser = (AdminUser) this.getCurrentUser();
		valueOperationsService.set("userDeviceId_"+currentUser.getId(), deviceId);
		return new ResultData();
	} 
	
}
