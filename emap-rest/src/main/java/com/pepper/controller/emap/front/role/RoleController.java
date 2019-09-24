package com.pepper.controller.emap.front.role;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.dubbo.config.annotation.Reference;
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
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.console.role.Role;
import com.pepper.model.console.role.RoleMenu;
import com.pepper.model.console.role.RoleUser;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.console.menu.MenuService;
import com.pepper.service.console.role.RoleMenuService;
import com.pepper.service.console.role.RoleService;
import com.pepper.service.console.role.RoleUserService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.util.MapToBeanUtil;

@Controller("frontRoleController")
@RequestMapping(value = "/front/role")
@Validated
public class RoleController  extends BaseControllerImpl implements BaseController {

	@Reference
	private RoleService roleService;
	
	@Reference
	private SystemLogService systemLogService;
	
	@Reference
	private RoleUserService roleUserService;
	
	@Reference
	private AdminUserService adminUserService;
	
	@Reference
	private RoleMenuService roleMenuService;
	
	@Reference
	private MenuService menuService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(Boolean isDefault) {
		ResultData resultData = new ResultData();
		Pager<Role> pager = new Pager<>();
//		pager.setPageNo(1);
//		pager.setPageSize(Integer.MAX_VALUE);
		pager.getJpqlParameter().setSearchParameter(SearchConstant.IS_TRUE+"_isIsms",true);
		if(isDefault!=null && isDefault) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.IS_TRUE+"_isDefault",true);
		}else if(isDefault!=null && !isDefault){
			pager.getJpqlParameter().setSearchParameter(SearchConstant.IS_FALSE+"_isDefault",false);
		}
		pager =  roleService.findNavigator(pager);
		resultData.setData("role", pager.getResults());
		systemLogService.log("role list", this.request.getRequestURL().toString());
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
		role.setIsDefault(false);
		if(this.roleService.findByCode(role.getCode())!=null) {
			resultData.setMessage(Internationalization.getMessageInternationalization(1400001));
			resultData.setCode(1400001);
			return resultData;
		}
		role = roleService.save(role);
		if(map.containsKey("menuIds")) {
			Map<String,Object> roleMenuMap= new HashMap<String, Object>();
			roleMenuMap.put("roleId", role.getId());
			roleMenuMap.put("menuIds", map.get("menuIds"));
			this.roleMenu(roleMenuMap);
		}
		systemLogService.log("add role ", this.request.getRequestURL().toString());
		return resultData;
	}
	
	
	@RequestMapping(value = "update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Role role = new Role();
		MapToBeanUtil.convert(role, map);
		role.setIsIsms(true);
		Role oldRole = roleService.findByCode(role.getCode());
		if(oldRole!=null && !oldRole.getId().equals(oldRole.getId())) {
			resultData.setMessage(Internationalization.getMessageInternationalization(1400001));
			resultData.setCode(1400001);
			return resultData;
		}
		roleService.update(role);
		if(map.containsKey("menuIds")) {
			Map<String,Object> roleMenuMap= new HashMap<String, Object>();
			roleMenuMap.put("roleId", role.getId());
			roleMenuMap.put("menuIds", map.get("menuIds"));
			this.roleMenu(roleMenuMap);
		}
		systemLogService.log("update role ", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		Role role = this.roleService.findById(id);
		resultData.setData("role", role);
		resultData.setData("menu", this.menuService.queryMenu("0", true));
		resultData.setData("roleMenuId", roleMenuService.findMenuIdsByRoleId(id));
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
				Role role = this.roleService.findById(id);
				if(!role.getIsDefault()) {
					roleService.deleteById(id);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("delete role ", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/user")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object roleUser(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		RoleUser roleUser = new RoleUser();
		MapToBeanUtil.convert(roleUser, map);
		RoleUser oldRoleUser = this.roleUserService.findByRoleIdAndUserId(roleUser.getRoleId(), roleUser.getUserId());
		if(oldRoleUser!=null) {
			resultData.setMessage(Internationalization.getMessageInternationalization(1500001));
			resultData.setCode(1500001);
			return resultData;
		}
		
		roleUserService.save(roleUser);
		return resultData;
	}
	
	@RequestMapping(value = "/user/delete")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object roleUserDelete(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		RoleUser roleUser = this.roleUserService.findByRoleIdAndUserId(map.get("roleId").toString(),map.get("userId").toString());
		String roleId = roleUser.getRoleId();
		Role role = this.roleService.findById(roleId);
		if(!role.getIsDefault()) {
			this.roleUserService.deleteById(roleUser.getId());
		}else {
			resultData.setMessage(Internationalization.getMessageInternationalization(1500002));
			resultData.setCode(1500002);
			return resultData;
		}
		return resultData;
	}
	
	@RequestMapping(value = "/user/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object roleUserList(String userId,String roleId) {
		ResultData resultData = new ResultData();
		Pager<RoleUser> pager = new Pager<RoleUser>();
		pager.setPageNo(1);
		pager.setPageSize(Integer.MAX_VALUE);
		if(StringUtils.hasText(userId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_userId",userId);
		}
		if(StringUtils.hasText(roleId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_roleId",roleId);
		}
		pager =  roleUserService.findNavigator(pager);
		List<RoleUser> list = pager.getResults();
		List returnList = new  ArrayList();
		for(RoleUser roleUser : list) {
			if(StringUtils.hasText(userId)) {
				Role role = this.roleService.findById(roleUser.getRoleId());
				returnList.add(role);
			}
			if(StringUtils.hasText(roleId)) {
				AdminUser adminUser = this.adminUserService.findById(roleUser.getUserId());
				returnList.add(adminUser);
			}
		}
		resultData.setData("list", returnList);
		systemLogService.log("roleUser list", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/menu")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object roleMenu(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Role role = this.roleService.findById(map.get("roleId").toString());
		if(Objects.equals(role.getCode(), "SUPER_ROLE")) {
			resultData.setCode(1500003);
			resultData.setMessage(Internationalization.getMessageInternationalization(1500003));
			return resultData;
		}
		this.roleMenuService.deleteByRoleId(map.get("roleId").toString());
		for(String menuId : (List<String>)map.get("menuIds")) {
			RoleMenu roleMenu = new RoleMenu();
			roleMenu.setRoleId(map.get("roleId").toString());
			roleMenu.setMenuId(menuId);
			roleMenuService.save(roleMenu);
		}
		return resultData;
	}
	
	@RequestMapping(value = "/menu/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object roleMenuList(String roleId) {
		ResultData resultData = new ResultData();
		List<String> list = this.roleMenuService.findMenuIdsByRoleId(roleId);
		resultData.setData("menu", this.menuService.queryMenu("0", true));
		resultData.setData("roleMenuId", list);
		return resultData;
	}
}
