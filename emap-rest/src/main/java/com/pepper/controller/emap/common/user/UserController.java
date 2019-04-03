package com.pepper.controller.emap.common.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pepper.common.emuns.Status;
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
import com.pepper.util.MapToBeanUtil;
import com.pepper.util.Md5Util;

@Controller("commonUserController")
@RequestMapping(value = "/common/user")
public class UserController extends BaseControllerImpl implements BaseController {

	@Reference
	private AdminUserService adminUserService;
	
	@Reference
	private RoleUserService roleUserService;
	
	@Reference
	private RoleService roleService;
	
	@Reference
	private DepartmentService departmentService;
	
	@Reference
	private FileService fileService;
	
	@RequestMapping(value = "/headPortrait")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object headPortrait(@RequestBody java.util.Map<String,Object> map) {
		ResultData resultData = new ResultData();
		AdminUser adminUser = (AdminUser) this.getCurrentUser();
		adminUser = adminUserService.findById(adminUser.getId());
		if(map.containsKey("headPortrait")) {
			adminUser.setHeadPortrait(map.get("headPortrait").toString());
		}else {
			adminUser.setHeadPortrait(null);
		}
		adminUserService.update(adminUser);
		return resultData;
	}
	
	
}
