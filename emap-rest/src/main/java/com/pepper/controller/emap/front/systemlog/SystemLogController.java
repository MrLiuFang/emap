package com.pepper.controller.emap.front.systemlog;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
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
	public Object list() {
		Pager<SystemLog> pager = new Pager<SystemLog>();
		pager = systemLogService.findNavigator(pager);
		pager.setData("systemlog",pager.getResults());
		pager.setResults(null);
		return pager;
	}
}
