package com.pepper.controller.emap.app.user;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.service.authentication.aop.Authorize;

@Controller("appUserController")
@RequestMapping(value = "/app/user")
@Validated
public class UserController extends BaseControllerImpl implements BaseController {

	
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
	
	
}
