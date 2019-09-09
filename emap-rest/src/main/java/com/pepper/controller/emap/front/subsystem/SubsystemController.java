package com.pepper.controller.emap.front.subsystem;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.staff.Staff;
import com.pepper.model.emap.subsystem.Subsystem;
import com.pepper.model.emap.vo.StaffVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.subsystem.SubsystemService;

@Controller()
@RequestMapping(value = "/front/subsystem")
public class SubsystemController extends BaseControllerImpl implements BaseController{
	
	@Reference
	private SubsystemService subsystemService;

	@Reference
	private SystemLogService systemLogService;
	
	private Pager< Subsystem> getPager(String name,Boolean isOnLine, Boolean isExport) {
		Pager< Subsystem> pager = new Pager< Subsystem>();
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_name",name );
		}
		if(isOnLine!=null&&isOnLine) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.IS_TRUE+"_isOnLine",isOnLine );
		}
		
		pager = subsystemService.findNavigator(pager);
		pager.setData("subsystem",pager.getResults());
		pager.setResults(null);
		return pager;
	}
	
	
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String name,Boolean isOnLine) {
		systemLogService.log("get subsystem list", this.request.getRequestURL().toString());
		return getPager(name, isOnLine, false);
	}
}
