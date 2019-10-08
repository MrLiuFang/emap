package com.pepper.controller.emap.front.systemlog;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.servlet.ServletOutputStream;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.common.emuns.Gender;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.ExcelColumn;
import com.pepper.controller.emap.util.ExportExcelUtil;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.log.SystemLog;
import com.pepper.model.emap.staff.Staff;
import com.pepper.model.emap.vo.StaffVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.log.SystemLogService;

@Controller()
@RequestMapping(value = "/front/systemlog")
public class SystemLogController extends BaseControllerImpl implements BaseController {

	
	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping(value = "/export")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public void export(String userName,String account,String role,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startDate,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endDate) throws IOException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		systemLogService.log("staff export", this.request.getRequestURL().toString());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xlsx");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("systemlog.xlsx", "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		Pager<SystemLog> pager = getPager(userName, account, role, startDate, endDate, true);
		List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
		excelColumn.add(ExcelColumn.build("姓名", "userName"));
		excelColumn.add(ExcelColumn.build("賬號", "account"));
		excelColumn.add(ExcelColumn.build("角色名稱", "roleName"));
		excelColumn.add(ExcelColumn.build("角色編碼", "roleCode"));
		excelColumn.add(ExcelColumn.build("地址", "url"));
		excelColumn.add(ExcelColumn.build("數據", "data"));
		new ExportExcelUtil().export((Collection<?>) pager.getData().get("systemlog"), outputStream, excelColumn);
	}
	
	private Pager<SystemLog> getPager(String userName,String account,String role, Date startDate, Date endDate, Boolean isExport) {
		Pager<SystemLog> pager = new Pager<SystemLog>();
		if (Objects.equals(isExport, true)) {
			pager.setPageNo(1);
			pager.setPageSize(500);
		}
		if(StringUtils.hasText(userName)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_userName", userName);
		}
		if(StringUtils.hasText(account)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_account", account);
		}
		if(StringUtils.hasText(role)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.OR_LIKE+"_roleCode&roleName&roleId", role);
		}
		if(startDate!=null) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.GREATER_THAN_OR_EQUAL_TO+"_createDate", startDate);
		}
		if(endDate!=null) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LESS_THAN_OR_EQUAL_TO+"_createDate", endDate);
		}
		pager.getJpqlParameter().setSortParameter("createDate", Direction.DESC);
		pager = systemLogService.findNavigator(pager);
		pager.setData("systemlog",pager.getResults());
		pager.setResults(null);
		return pager;
	}
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String userName,String account,String role,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startDate,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endDate) {
		systemLogService.log("get system log list", this.request.getRequestURI());
		
		return getPager(userName, account, role, startDate, endDate, false);
	}
	
	
	@RequestMapping(value = "/delete")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object delete(@RequestBody String str) throws IOException {
		ResultData resultData = new ResultData();
		if(!StringUtils.hasText(str)){
			return resultData;
		}
		JsonNode jsonNode = new ObjectMapper().readTree(str);
		if(!jsonNode.has("id")) {
			return resultData;
		}
		ArrayNode arrayNode = (ArrayNode)jsonNode.get("id");
		for(int i = 0; i <arrayNode.size(); i++) {
			String id = arrayNode.get(i).asText();
			try {
				systemLogService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("systemLog delete", this.request.getRequestURL().toString());
		return resultData;
	}
}
