package com.pepper.controller.emap.front.subsystem;

import java.io.IOException;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.subsystem.Subsystem;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.subsystem.SubsystemService;
import com.pepper.util.MapToBeanUtil;

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
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Subsystem subsystem = new Subsystem();
		MapToBeanUtil.convert(subsystem, map);
		subsystemService.save(subsystem);
		systemLogService.log("site subsystem", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Subsystem subsystem = new Subsystem();
		MapToBeanUtil.convert(subsystem, map);
		
		subsystemService.update(subsystem);
		systemLogService.log("site subsystem", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		resultData.setData("subsystem",subsystemService.findById(id));
		systemLogService.log("get subsystem info", this.request.getRequestURL().toString());
		return resultData;
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
				subsystemService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("subsystem delete", this.request.getRequestURL().toString());
		return resultData;
	}
}
