package com.pepper.controller.emap.front.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.pepper.common.emuns.Status;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.console.enums.UserType;
import com.pepper.model.console.role.Role;
import com.pepper.model.console.role.RoleUser;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.vo.AdminUserVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.console.role.RoleService;
import com.pepper.service.console.role.RoleUserService;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.service.file.FileService;
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
	
	@Reference
	private DepartmentService departmentService;
	
	@Reference
	private FileService fileService;
	
	@Reference
	private com.pepper.service.emap.event.EventListService eventListService;
	
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
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String account,String mobile,String email,String name,String departmentId) {
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
		if(StringUtils.hasText(departmentId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_departmentId",departmentId );
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
			if(roleUser!=null) {
				adminUserVo.setRole(roleService.findById(roleUser.getRoleId()));
			}
			if(StringUtils.hasText(user.getDepartmentId())) {
				adminUserVo.setDepartment(departmentService.findById(user.getDepartmentId()));
			}
			adminUserVo.setHeadPortraitUrl(fileService.getUrl(user.getHeadPortrait()));
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
		AdminUser oldAdminUser = adminUserService.findByAccount(adminUser.getAccount());
		if(oldAdminUser!=null) {
			resultData.setMessage(Internationalization.getMessageInternationalization(3000001));
			resultData.setCode(3000001);
			return resultData;
		}
				
		adminUser.setStatus(Status.NORMAL);
		adminUser.setUserType(UserType.EMPLOYEE);
		adminUser.setCreateDate(new Date());
		AdminUser user = (AdminUser) this.getCurrentUser();
		adminUser.setCreateUser(user.getId());
		adminUser.setPassword(Md5Util.encryptPassword(adminUser.getPassword().toUpperCase(),adminUser.getAccount()));
		adminUser.setStatus(Status.NORMAL);
		adminUser.setUserType(UserType.EMPLOYEE);
		adminUser.setIsWork(false);
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
		adminUser.setUpdateDate(new Date());
		AdminUser user = (AdminUser) this.getCurrentUser();
		adminUser.setUpdateUser(user.getId());
		// 账号不允许修改
		AdminUser old = adminUserService.findById(adminUser.getId());
		adminUser.setPassword(old.getPassword());
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
		if(adminUser!=null) {
			adminUser.setPassword("");
			resultData.setData("user",adminUser);
			resultData.setData("userRole", roleService.findById(roleUser.getRoleId()));
			resultData.setData("department", departmentService.findById(adminUser.getDepartmentId()));
		}
		return resultData;
	}
	
	@RequestMapping(value = "/handover")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object handover(@RequestBody Map<String,String> map) {
		ResultData resultData = new ResultData();
		AdminUser adminUser = adminUserService.findByAccountAndPassword(map.get("account"),map.get("password"));
		if(adminUser==null) {
			resultData.setCode(4000001);
			resultData.setMessage("用户认证失败！");
			return resultData;
		}
		
		Role role = roleService.findByUserId(adminUser.getId());
		if (role == null) {
			resultData.setCode(4000001);
			resultData.setMessage("用户认证失败！");
			return resultData;
		}
		
		if(role.getCode().equals("EMPLOYEE_ROLE")) {
			resultData.setMessage(Internationalization.getMessageInternationalization(4000002));
			resultData.setCode(4000002);
			return resultData;
		}
		
		AdminUser currentUser = (AdminUser) this.getCurrentUser();
		eventListService.handover(adminUser.getId(), currentUser.getId());
		return resultData;
	}
	
	@RequestMapping(value = "/import")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importAdminUser(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		for (String fileName : files.keySet()) {
			MultipartFile file = files.get(fileName);
			Workbook wookbook = new HSSFWorkbook(file.getInputStream());
	        Sheet sheet = wookbook.getSheetAt(0);
	        Row rowHead = sheet.getRow(0);
			int totalRowNum = sheet.getLastRowNum();
			for(int i = 1 ; i <= totalRowNum ; i++)
	        {
				Row row = sheet.getRow(i);
				AdminUser adminUser = new AdminUser();
				adminUser.setAccount(getCellValue(row.getCell(0)).toString());
				adminUser.setName(getCellValue(row.getCell(1)).toString());
				adminUser.setEmail(getCellValue(row.getCell(2)).toString());
				adminUser.setMobile(getCellValue(row.getCell(3)).toString());
				adminUser.setNickName(getCellValue(row.getCell(4)).toString());
				adminUser.setPassword("96E79218965EB72C92A549DD5A330112");
				AdminUser oldAdminUser = adminUserService.findByAccount(adminUser.getAccount());
				if(oldAdminUser!=null) {
					continue;
				}
						
				adminUser.setStatus(Status.NORMAL);
				adminUser.setUserType(UserType.EMPLOYEE);
				adminUser.setCreateDate(new Date());
				AdminUser user = (AdminUser) this.getCurrentUser();
				adminUser.setCreateUser(user.getId());
				adminUser.setPassword(Md5Util.encryptPassword(adminUser.getPassword().toUpperCase(),adminUser.getAccount()));
				adminUser.setUserType(UserType.EMPLOYEE);
				adminUser.setIsWork(false);
				adminUserService.save(adminUser);
				
	        }
		}
		return resultData;
	}
	
	private Object getCellValue(Cell cell) {
		if(cell == null) {
			return "";
		}
		Object object = null;
		switch (cell.getCellType()) {
		case STRING :
			object = cell.getStringCellValue();
			break;
		case NUMERIC :
			object = cell.getNumericCellValue();
			break;
		case BOOLEAN :
			object = cell.getBooleanCellValue();
			break;
		default:
			break;
		}
		return object;
	}
	
}
