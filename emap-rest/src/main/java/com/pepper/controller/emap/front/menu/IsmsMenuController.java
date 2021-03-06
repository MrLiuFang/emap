package com.pepper.controller.emap.front.menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.common.emuns.Status;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.model.console.enums.MenuType;
import com.pepper.model.console.menu.Menu;
import com.pepper.model.console.menu.MenuVo;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.menu.MenuService;
import com.pepper.service.console.role.RoleMenuService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.util.MapToBeanUtil;

@Controller
@RequestMapping(value = "/front/menu")
public class IsmsMenuController extends BaseControllerImpl implements BaseController {

	@Reference
	private MenuService menuService;
	
	@Reference
	private RoleMenuService roleMenuService;
	
	@Reference
	private SystemLogService systemLogService;
	
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String parentId,MenuType menuType) {
		ResultData resultData = new ResultData();
		List<MenuVo> list = this.menuService.queryMenu(parentId, true, menuType);
		resultData.setData("menu",list);
//		systemLogService.log("get menuCatalog list", this.request.getRequestURL().toString());
		return resultData;
	}
	
	
	
	@RequestMapping(value = "add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Menu menu = new Menu();
		MapToBeanUtil.convert(menu, map);
		if(map.containsKey("menuType")&&Objects.nonNull(map.get("menuType"))) {
			menu.setMenuType(MenuType.valueOf(map.get("menuType").toString()));
		}
//		menu.setParentId("0");
		if(Objects.isNull(menu.getParentId())) {
			menu.setParentId("0");
		}
		
		menu.setStatus(Status.NORMAL);
		menu.setIsIsms(true);
		if(menuService.findByCode(menu.getCode())!=null) {
			resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
			resultData.setCode(2000001);
			return resultData;
		}
		this.menuService.save(menu);
		systemLogService.log("add menu ", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		resultData.setData("menu",menuService.findById(id));
//		systemLogService.log("get menu info", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Menu menu = new Menu();
		MapToBeanUtil.convert(menu, map);
		if(map.containsKey("menuType")&&Objects.nonNull(map.get("menuType"))) {
			menu.setMenuType(MenuType.valueOf(map.get("menuType").toString()));
		}
		menu.setIsIsms(true);
//		if(!menu.getParentId().equals("0")) {
//			Menu menu1 = menuService.findByUrl(menu.getUrl());
//			if(menu1!=null && !menu1.getId().equals(menu.getId()) && !menu1.getParentId().equals("0")) {
//				resultData.setMessage(Internationalization.getMessageInternationalization(1300001));
//				resultData.setCode(1300001);
//				return resultData;
//			}
//		}
		
		Menu oldMenu = menuService.findByCode(menu.getCode());
		if(oldMenu!=null && oldMenu.getCode()!=null&&menu.getCode()!=null) {
			if(!menu.getId().equals(oldMenu.getId())){
				resultData.setCode(2000001);
				resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
				return resultData;
			}
		}
		
		if(Objects.equals(menu.getParentId(), menu.getId())) {
			resultData.setMessage(Internationalization.getMessageInternationalization(1600001));
			resultData.setCode(1600001);
			return resultData;
		}
		this.menuService.update(menu);
		systemLogService.log("update menu ", this.request.getRequestURL().toString());
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
				List<MenuVo> list = this.menuService.queryMenu(id, true,null);
				menuService.deleteById(id);
				deleteMenu(list);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("delete menu ", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private void deleteMenu(List<MenuVo> list) {
		for(MenuVo menuVo : list) {
			menuService.deleteById(menuVo.getId());
			roleMenuService.deleteByMenuId(menuVo.getId());
			if(Objects.nonNull(menuVo.getChild())&& menuVo.getChild().size()>0) {
				deleteMenu(menuVo.getChild());
			}
		}
	}
}
