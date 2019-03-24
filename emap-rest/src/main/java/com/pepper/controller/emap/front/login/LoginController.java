package com.pepper.controller.emap.front.login;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pepper.core.ResultData;
import com.pepper.core.ResultEnum.Status;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.GlobalConstant;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.console.role.Role;
import com.pepper.service.authentication.FrontAuthorize;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.console.menu.MenuService;
import com.pepper.service.console.parameter.ParameterService;
import com.pepper.service.console.role.RoleService;
import com.pepper.service.console.role.RoleUserService;
import com.pepper.service.redis.string.serializer.SetOperationsService;
import com.pepper.service.redis.string.serializer.StringRedisTemplateService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;

@Controller("frontLoginController")
@RequestMapping(value = "/front")
@Validated
public class LoginController extends BaseControllerImpl implements BaseController {
	@SuppressWarnings("unused")
	@Autowired
	private Environment environment;

	@Reference
	private ParameterService parameterService;

	@Reference
	private AdminUserService adminUserService;

	@Reference
	private ValueOperationsService valueOperationsService;

	@Reference
	private StringRedisTemplateService stringRedisTemplateService;

	@Reference
	private SetOperationsService setOperationsService;

	@Reference
	private RoleUserService roleUserService;

	@Reference
	private RoleService roleService;

	@Reference
	private MenuService menuService;

	@Autowired
	private FrontAuthorize frontAuthorize;

	@RequestMapping(value = "/login")
	@ResponseBody
	public ResultData login(@RequestBody AdminUser user) {
		ResultData resultData = new ResultData();

		if (!StringUtils.hasText(user.getAccount())) {
			resultData.setMessage("用戶名不能爲空");
			resultData.setStatus(Status.LOGIN_FAIL.getKey());
			resultData.setCode(1000001);
			return resultData;
		}
		if (!StringUtils.hasText(user.getPassword())) {
			resultData.setMessage("密碼不能爲空");
			resultData.setCode(1000002);
			return resultData;
		}

		AdminUser userReal = adminUserService.findByAccountAndPassword(user.getAccount(), user.getPassword());

		if (userReal == null) {
			resultData.setMessage("用戶名/密碼錯誤");
			resultData.setStatus(Status.LOGIN_FAIL.getKey());
			resultData.setCode(1000003);
			return resultData;
		}
		if ((userReal.getStatus() != null && com.pepper.common.emuns.Status.DISABLE.equals(userReal.getStatus()))
				|| userReal.getStatus() == null) {
			resultData.setMessage("用戶已被鎖定，請聯係管理員");
			resultData.setStatus(Status.LOGIN_FAIL.getKey());
			resultData.setCode(1000004);
			return resultData;
		}
		Role role = roleService.findByUserId(userReal.getId());
		if (role == null) {
			resultData.setMessage("用戶無角色，請聯係管理員");
			resultData.setStatus(Status.LOGIN_FAIL.getKey());
			resultData.setCode(1000005);
			return resultData;
		}
		if (com.pepper.common.emuns.Status.DISABLE.equals(role.getStatus())) {
			resultData.setMessage("用戶所在角色已被禁用，請聯係管理員");
			resultData.setStatus(Status.LOGIN_FAIL.getKey());
			resultData.setCode(1000006);
			return resultData;
		}

		// 更新用户的最后登录时间
		userReal.setLastLoginTime(new Date());

		adminUserService.updateLoginTime(userReal.getId());

		// 获取用户所有资源，并让其处于登录状态。
		List<String> resourceList = roleService.queryUserAllResources(userReal.getId());
		String token = setLoginInfo(userReal, resourceList);
		resultData.setData("token", token);
		resultData.setData("role", role);
		
		return resultData;
	}

	private String setLoginInfo(AdminUser user, List<String> resourceList) {
		String token = UUID.randomUUID().toString();

		// 记录用户登录状态
		frontAuthorize.setAuthorizeInfo(user.getId(), token);
		// 先删除以前的权限资源
		frontAuthorize.deleteUserResources(user.getId());
		// 设置权限资源
		frontAuthorize.setUserResources(resourceList, user.getId());
		// 将当前用户信息保存到redis
		frontAuthorize.setCurrentUser(user.getId(), user);

		/**
		 * 写token cookie到前端
		 */
		Cookie cookieToken = new Cookie(GlobalConstant.AUTHORIZE_TOKEN, token);
		cookieToken.setMaxAge(-1);
		cookieToken.setPath("/");
		response.addCookie(cookieToken);

		/**
		 * 将用户资源放到redis中，用于jsp的鉴权标签
		 */
		List<String> authMenuCode = roleService.queryUserAllMenuCode(user.getId());
		if (authMenuCode != null && authMenuCode.size() > 0) {
			String[] authMenuCodeArr = new String[authMenuCode.size()];
			setOperationsService.add(GlobalConstant.USER_RESOURCE_CODE + user.getId(),
					authMenuCode.toArray(authMenuCodeArr));
		}

		return token;
	}

	/**
	 * 退出登录
	 * 
	 * @return
	 */
	@RequestMapping(value = "/loginOut")
	@Authorize(authorizeResources = false, authorizeLogin = false)
	@ResponseBody
	public ResultData loginOut() {
		ResultData resultData = new ResultData();
		String token = this.getCookie(GlobalConstant.AUTHORIZE_TOKEN);
		String userId = frontAuthorize.getUserId(token);
		frontAuthorize.deleteAuthorizeInfo(token);
		frontAuthorize.deleteUserResources(userId);
		frontAuthorize.deleteResourceCode(userId);
		return resultData;
	}
}
