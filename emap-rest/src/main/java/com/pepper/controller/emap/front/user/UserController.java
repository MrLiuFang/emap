package com.pepper.controller.emap.front.user;

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
import com.pepper.util.MapToBeanUtil;
import com.pepper.util.Md5Util;

@Controller("frontUserController")
@RequestMapping(value = "/front/user")
public class UserController extends BaseControllerImpl implements BaseController {

	@Reference
	private AdminUserService adminUserService;
	
	@Reference
	private RoleUserService roleUserService;
	
	@Reference
	private RoleService roleService;
	
	@RequestMapping(value = "/getUserInfo")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public ResultData getUserInfo() {
		ResultData resultData = new ResultData();
		AdminUser adminUser = (AdminUser) this.getCurrentUser();
		adminUser.setPassword("");
		resultData.setData("user", adminUser);
		return resultData;
	}
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String account,String mobile,String email,String name) {
		Pager<AdminUser> pager = new Pager<AdminUser>();
		pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_userType", UserType.EMPLOYEE);
		if(StringUtils.hasText(account)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_account",account );
		}
		if(StringUtils.hasText(mobile)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_mobile",mobile );
		}
		if(StringUtils.hasText(email)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_email",email );
		}
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
		}
		pager = adminUserService.list(pager);
		Role role = null;
		for (AdminUser u : pager.getResults()) {
			role = roleService.findByUserId(u.getId());
			if (role!=null) {
				u.setCreateUser(role.getName());
			}
		}
		List<AdminUser> list = pager.getResults();
		List<AdminUserVo> returnList = new ArrayList<AdminUserVo>();
		for(AdminUser user : list) {
			AdminUserVo  adminUserVo = new AdminUserVo();
			BeanUtils.copyProperties(user, adminUserVo);
			adminUserVo.setPassword("");
			RoleUser roleUser = roleUserService.findByUserId(user.getId());
			adminUserVo.setRole(roleService.findById(roleUser.getRoleId()));
			returnList.add(adminUserVo);
		}
		pager.setData("user",returnList);
		pager.setResults(null);
		return pager;
	}
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		AdminUser adminUser = new AdminUser();
		MapToBeanUtil.convert(adminUser, map);
		adminUser.setStatus(Status.NORMAL);
		adminUser.setUserType(UserType.EMPLOYEE);
		adminUser.setCreateDate(new Date());
		AdminUser user = (AdminUser) this.getCurrentUser();
		adminUser.setCreateUser(user.getId());
		adminUser.setPassword(Md5Util.encryptPassword(Md5Util.encodeByMD5(adminUser.getPassword()),adminUser.getAccount()));
		adminUserService.saveUser(adminUser, map.get("roleId").toString());
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		AdminUser adminUser = new AdminUser();
		MapToBeanUtil.convert(adminUser, map);
		adminUser.setPassword(null);
		adminUser.setUpdateDate(new Date());
		AdminUser user = (AdminUser) this.getCurrentUser();
		adminUser.setUpdateUser(user.getId());
		// 账号不允许修改
		AdminUser old = adminUserService.findById(adminUser.getId());
		adminUser.setAccount(old.getAccount());
		adminUserService.updateUser(adminUser, map.get("roleId").toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit( String userId) {
		ResultData resultData = new ResultData();
		RoleUser roleUser = roleUserService.findByUserId(userId);
		AdminUser adminUser = adminUserService.findById(userId);
		adminUser.setPassword("");
		resultData.setData("user",adminUser);
		resultData.setData("userRole", roleService.findById(roleUser.getRoleId()));
		return resultData;
	}
	
}
