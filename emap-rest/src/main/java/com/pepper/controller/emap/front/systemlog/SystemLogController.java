package com.pepper.controller.emap.front.systemlog;

import java.util.Date;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.log.SystemLog;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.log.SystemLogService;

@Controller()
@RequestMapping(value = "/front/systemlog")
public class SystemLogController extends BaseControllerImpl implements BaseController {

	
	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String userName,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startDate,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endDate) {
		systemLogService.log("get system log list", this.request.getRequestURI());
		Pager<SystemLog> pager = new Pager<SystemLog>();
		if(StringUtils.hasText(userName)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_userName", userName);
		}
		if(startDate!=null) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.GREATER_THAN_OR_EQUAL_TO+"_createDate", startDate);
		}
		if(endDate!=null) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LESS_THAN_OR_EQUAL_TO+"_createDate", endDate);
		}
		pager.getJpqlParameter().setSortParameter("createDate", "DESC");
		pager = systemLogService.findNavigator(pager);
		pager.setData("systemlog",pager.getResults());
		pager.setResults(null);
		return pager;
	}
}
