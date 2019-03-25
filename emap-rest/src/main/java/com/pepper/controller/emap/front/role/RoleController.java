package com.pepper.controller.emap.front.role;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.Pager;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.console.role.Role;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.role.RoleService;

@Controller("frontRoleController")
@RequestMapping(value = "/front/role")
@Validated
public class RoleController {

	@Reference
	private RoleService roleService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public ResultData getRoleLit() {
		ResultData resultData = new ResultData();
		Pager<Role> pager = new Pager<>();
		pager.setPageNo(1);
		pager.setPageSize(Integer.MAX_VALUE);
		pager.getJpqlParameter().setSearchParameter(SearchConstant.NOTIN+"_code", new String[]{"ADMIN","SUPER_ADMIN_ROLE"});
		pager =  roleService.findNavigator(pager);
		resultData.setData("role", pager.getResults());
		return resultData;
	}
}
