package com.pepper.controller.emap.front.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.console.enums.UserType;
import com.pepper.model.console.role.Role;
import com.pepper.model.console.role.RoleUser;
import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.department.DepartmentGroup;
import com.pepper.model.emap.vo.AdminUserVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.console.role.RoleService;
import com.pepper.service.console.role.RoleUserService;
import com.pepper.service.emap.department.DepartmentGroupService;
import com.pepper.service.emap.department.DepartmentService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.file.FileService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;
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
	private DepartmentGroupService departmentGroupService;
	
	@Reference
	private FileService fileService;
	
	@Reference
	private com.pepper.service.emap.event.EventListService eventListService;
	
	@Reference
	private com.pepper.service.redis.jdk.serializer.ValueOperationsService jdkValueOperationsService;
	
	@Reference
	private ValueOperationsService stringValueOperationsService;
	
	@Reference
	private SystemLogService systemLogService;
	
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
		systemLogService.log("get user info", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String account,String mobile,String email,String name,String departmentId,String departmentGroupId,String roleId) {
		Pager<AdminUser> pager = new Pager<AdminUser>();
		
		pager = adminUserService.findAdminUser(pager,account, mobile, email, name, departmentId, departmentGroupId, roleId);
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
			if(StringUtils.hasText(user.getDepartmentGroupId())) {
				adminUserVo.setDepartmentGroup(departmentGroupService.findById(user.getDepartmentGroupId()));
			}
			adminUserVo.setHeadPortraitUrl(fileService.getUrl(user.getHeadPortrait()));
			returnList.add(adminUserVo);
		}
		pager.setData("user",returnList);
		pager.setResults(null);
		systemLogService.log("get user list", this.request.getRequestURL().toString());
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
		systemLogService.log("get user add", this.request.getRequestURL().toString());
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
		systemLogService.log("get user update", this.request.getRequestURL().toString());
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
			resultData.setData("departmentGroup",departmentGroupService.findById(adminUser.getDepartmentGroupId()));
		}
		systemLogService.log("get user to edit", this.request.getRequestURL().toString());
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
		systemLogService.log("work handover", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@SuppressWarnings("resource")
	@RequestMapping(value = "/import")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importAdminUser(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		List<Map<String,AdminUser>> list = new ArrayList<Map<String,AdminUser>>();
		for (String fileName : files.keySet()) {
			MultipartFile file = files.get(fileName);
			Workbook wookbook = new XSSFWorkbook(file.getInputStream());
	        Sheet sheet = wookbook.getSheetAt(0);
	        Row rowHead = sheet.getRow(0);
			int totalRowNum = sheet.getLastRowNum();
			if(!check(sheet.getRow(0))) {
				resultData.setCode(4000003);
				resultData.setMessage("数据错误！（非用户结构数据）");
				return resultData;
			}
			
			
			for(int i = 1 ; i <= totalRowNum ; i++){
				Row row = sheet.getRow(i);
				AdminUser adminUser = new AdminUser();
				adminUser.setAccount(getCellValue(row.getCell(0)).toString());
				adminUser.setName(getCellValue(row.getCell(1)).toString());
				adminUser.setEmail(getCellValue(row.getCell(2)).toString());
				adminUser.setMobile(getCellValue(row.getCell(3)).toString());
				adminUser.setNickName(getCellValue(row.getCell(4)).toString());
				adminUser.setPassword("E10ADC3949BA59ABBE56E057F20F883E");						
				adminUser.setStatus(Status.NORMAL);
				adminUser.setUserType(UserType.EMPLOYEE);
				adminUser.setCreateDate(new Date());
				AdminUser user = (AdminUser) this.getCurrentUser();
				adminUser.setCreateUser(user.getId());
				adminUser.setPassword(Md5Util.encryptPassword(adminUser.getPassword().toUpperCase(),adminUser.getAccount()));
				adminUser.setUserType(UserType.EMPLOYEE);
				adminUser.setIsWork(false);
				
				
				if(!StringUtils.hasText(adminUser.getAccount())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，account不能为空");
					return resultData;
				}
				
				if(!StringUtils.hasText(adminUser.getName())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，name不能为空");
					return resultData;
				}
//				if(!StringUtils.hasText(adminUser.getEmail())) {
//					resultData.setCode(4000003);
//					resultData.setMessage("数据错误！第"+i+"行，email不能为空");
//					return resultData;
//				}
//				if(!StringUtils.hasText(adminUser.getMobile())) {
//					resultData.setCode(4000003);
//					resultData.setMessage("数据错误！第"+i+"行，mobile不能为空");
//					return resultData;
//				}
//				if(!StringUtils.hasText(adminUser.getNickName())) {
//					resultData.setCode(4000003);
//					resultData.setMessage("数据错误！第"+i+"行，nickName不能为空");
//					return resultData;
//				}
				
				
				
				if(!StringUtils.hasText(getCellValue(row.getCell(5)).toString())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，role不能为空");
					return resultData;
				}
				
				if(this.adminUserService.findByAccount(adminUser.getAccount())!=null) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，"+adminUser.getAccount()+"已存在！");
					return resultData;
				}
				
				
				String roleName = getCellValue(row.getCell(5)).toString();
				Role role = this.roleService.findByName(roleName);
				if(role==null) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，role角色错误！");
					return resultData;
				}
				
				if(role.getCode().equals("EMPLOYEE_ROLE")) {
					String departmentName= getCellValue(row.getCell(6)).toString();
					String departmentGroupName= getCellValue(row.getCell(7)).toString();
					String isManager= getCellValue(row.getCell(8)).toString();
					if(StringUtils.hasText(isManager)&&!(isManager.toLowerCase().equals("true")||isManager.toLowerCase().equals("false"))) {
						resultData.setCode(4000006);
						resultData.setMessage("数据错误！第"+i+"行，isManager数据错误！");
						return resultData;
					}
					if(!StringUtils.hasText(departmentName)) {
						resultData.setCode(4000004);
						resultData.setMessage("数据错误！第"+i+"行，department不能为空！");
						return resultData;
					}else {
						List<Department> listDepartment = this.departmentService.findByName(departmentName);
						if(listDepartment.size()!=1) {
							resultData.setCode(4000005);
							resultData.setMessage("数据错误！第"+i+"行，department错误（找到多个/没有找到部门）！");
							return resultData;
						}else {
							Department department = listDepartment.get(0);
							List<DepartmentGroup> listDepartmentGroup = this.departmentGroupService.findByDepartmentIdAndName(department.getId(), departmentGroupName);
							if(StringUtils.hasText(departmentGroupName)&& listDepartmentGroup.size()!=1) {
								resultData.setCode(4000005);
								resultData.setMessage("数据错误！第"+i+"行，DepartmentGroup错误（找到多个/没有找到部门组）！");
								return resultData;
							}else {
								adminUser.setDepartmentId(department.getId());
								adminUser.setDepartmentGroupId(listDepartmentGroup.size()==1?listDepartmentGroup.get(0).getId():null);
								adminUser.setIsManager(Boolean.valueOf(StringUtils.hasText(isManager)?isManager.toLowerCase():"false"));
							}
						}
					}
				}
				
				String roleId = role.getId();
				Map<String,AdminUser> map = new HashMap<String, AdminUser>();
				map.put(roleId, adminUser);
				list.add(map);
	        }
			for(Map<String,AdminUser> map : list) {
				for (String key : map.keySet()) {
					adminUserService.saveUser(map.get(key), key);
				}
				
			}
			
		}
		systemLogService.log("user import", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private Boolean check(Row row) {
		if(!getCellValue(row.getCell(0)).toString().equals("account")) {
			return false;
		}
		if(!getCellValue(row.getCell(1)).toString().equals("name")) {
			return false;
		}
		if(!getCellValue(row.getCell(2)).toString().equals("email")) {
			return false;
		}
		if(!getCellValue(row.getCell(3)).toString().equals("mobile")) {
			return false;
		}
		if(!getCellValue(row.getCell(4)).toString().equals("nickName")) {
			return false;
		}
		if(!getCellValue(row.getCell(5)).toString().equals("role")) {
			return false;
		}
		if(!getCellValue(row.getCell(6)).toString().equals("department")) {
			return false;
		}
		if(!getCellValue(row.getCell(7)).toString().equals("departmentGroup")) {
			return false;
		}
		if(!getCellValue(row.getCell(8)).toString().equals("isManager")) {
			return false;
		}
		return true;
	}
	
	@RequestMapping(value = "/headPortrait") 
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object headPortrait(@RequestBody java.util.Map<String,Object> map) {
		ResultData resultData = new ResultData();
//		AdminUser adminUser = (AdminUser) this.getCurrentUser();
		AdminUser adminUser = adminUserService.findById(map.get("id").toString());
		if(map.containsKey("headPortrait")) {
			adminUser.setHeadPortrait(map.get("headPortrait").toString());
		}else {
			adminUser.setHeadPortrait(null);
		}
		adminUserService.update(adminUser);
		jdkValueOperationsService.set(adminUser.getId(), adminUser);
		systemLogService.log("user update head portrait", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private Object getCellValue(Cell cell) {
		if(cell == null) {
			return "";
		}
		Object object = "";
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
