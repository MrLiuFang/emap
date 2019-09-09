package com.pepper.controller.emap.front.role;

import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.console.role.Role;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.role.RoleService;
import com.pepper.util.MapToBeanUtil;

@Controller("frontRoleController")
@RequestMapping(value = "/front/role")
@Validated
public class RoleController {

	@Reference
	private RoleService roleService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list() {
		ResultData resultData = new ResultData();
		Pager<Role> pager = new Pager<>();
		pager.setPageNo(1);
		pager.setPageSize(Integer.MAX_VALUE);
		pager.getJpqlParameter().setSearchParameter(SearchConstant.IS_TRUE+"_isIsms",true);
		pager =  roleService.findNavigator(pager);
		resultData.setData("role", pager.getResults());
		return resultData;
	}
	
	@RequestMapping(value = "add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Role role = new Role();
		MapToBeanUtil.convert(role, map);
		role.setIsIsms(true);
		if(this.roleService.findByCode(role.getCode())!=null) {
			resultData.setMessage(Internationalization.getMessageInternationalization(1400001));
			resultData.setCode(1400001);
			return resultData;
		}
		roleService.save(role);
		return resultData;
	}
	
	@RequestMapping(value = "/roleUser")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object roleUser() {
		ResultData resultData = new ResultData();
		
		return resultData;
	}
	
	@RequestMapping(value = "/roleMenu")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object roleMenu() {
		ResultData resultData = new ResultData();
		
		return resultData;
	}
}
