package com.pepper.controller.emap.common.user;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pepper.common.emuns.Status;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.map.Map;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.console.role.RoleService;
import com.pepper.service.console.role.RoleUserService;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.service.file.FileService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;
import com.pepper.util.Md5Util;

@Controller("commonUserController")
@RequestMapping(value = {"/common/user"})
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
	
	@Reference
	private com.pepper.service.redis.jdk.serializer.ValueOperationsService jdkValueOperationsService;
	
	@Reference
	private ValueOperationsService StringValueOperationsService;
	
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
		
		jdkValueOperationsService.set(adminUser.getId(), adminUser);
		return resultData;
	}
	
	@RequestMapping(value = "/updatePassword")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object updatePassword(@RequestBody java.util.Map<String,Object> map) {
		ResultData resultData = new ResultData();
		String id = "";
		AdminUser adminUser= null;
		if(map.containsKey("userId")) {
			id=map.get("userId").toString();
		}else {
			adminUser = (AdminUser) this.getCurrentUser();
			id = adminUser.getId();
		}
		String password = map.get("password").toString();
		if(StringUtils.hasText(id)) {
			adminUser = adminUserService.findById(id);
			if(adminUser!=null && StringUtils.hasText(password)) {
				adminUser.setPassword(Md5Util.encryptPassword(password.toUpperCase(),adminUser.getAccount()));
				adminUserService.update(adminUser);
			}
		}else {
			resultData.setCode(300001);
			resultData.setMessage("获取要修改的用户数据错误！");
		}
		if(!StringUtils.hasText(password)) {
			resultData.setCode(300001);
			resultData.setMessage("密码不能为空！");
		}
		return resultData;
	}
	
	@RequestMapping(value = "/language")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object language(@RequestBody java.util.Map<String,String> map) {
		ResultData resultData = new ResultData();
		AdminUser adminUser = (AdminUser) this.getCurrentUser();
		StringValueOperationsService.set(adminUser.getId()+"_language", map.get("language")==null?"zh":map.get("language").toString() );
		return resultData;
	}
	
}
