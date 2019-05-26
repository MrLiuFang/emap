package com.pepper.controller.emap.front.site;

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
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.site.SiteInfoService;
import com.pepper.util.MapToBeanUtil;

@Controller()
@RequestMapping(value = "/front/site")
public class SiteController  extends BaseControllerImpl implements BaseController {
	
	@Reference
	private SiteInfoService siteInfoService;
	
	@Reference
	private SystemLogService systemLogService;

	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String code,String name,String keyWord) {
		Pager<SiteInfo> pager = new Pager<SiteInfo>();
		if(StringUtils.hasText(code)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_code",code );
		}
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
		}
		if(StringUtils.hasText(keyWord)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.ORLIKE+"_code&name",keyWord );
		}
		pager = siteInfoService.findNavigator(pager);
		
		pager.setData("site",pager.getResults());
		pager.setResults(null);
		systemLogService.log("get site list", this.request.getRequestURL().toString());
		return pager;
	}
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		SiteInfo siteInfo = new SiteInfo();
		MapToBeanUtil.convert(siteInfo, map);
		
		if(siteInfoService.findByCode(siteInfo.getCode())!=null) {
			resultData.setCode(2000001);
			resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
			return resultData;
		}
		
		siteInfoService.save(siteInfo);
		systemLogService.log("site add", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		SiteInfo siteInfo = new SiteInfo();
		MapToBeanUtil.convert(siteInfo, map);
		
		SiteInfo oldSiteInfo = siteInfoService.findByCode(siteInfo.getCode());
		if(oldSiteInfo!=null && oldSiteInfo.getCode()!=null&&siteInfo.getCode()!=null) {
			if(!siteInfo.getId().equals(oldSiteInfo.getId())){
				resultData.setCode(2000001);
				resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
				return resultData;
			}
		}
		siteInfoService.update(siteInfo);
		systemLogService.log("site update", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		resultData.setData("site",siteInfoService.findById(id));
		systemLogService.log("get site info", this.request.getRequestURL().toString());
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
				siteInfoService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("site delete", this.request.getRequestURL().toString());
		return resultData;
	}
}
