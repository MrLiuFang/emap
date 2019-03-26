package com.pepper.controller.emap.front.site;

import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.site.SiteInfoService;
import com.pepper.util.MapToBeanUtil;

@Controller()
@RequestMapping(value = "/front/site")
public class SiteController  extends BaseControllerImpl implements BaseController {
	
	@Reference
	private SiteInfoService siteInfoService;

	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String code,String name) {
		Pager<SiteInfo> pager = new Pager<SiteInfo>();
		if(StringUtils.hasText(code)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_code",code );
		}
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
		}
		pager = siteInfoService.findNavigator(pager);
		
		pager.setData("site",pager.getResults());
		pager.setResults(null);
		return pager;
	}
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		SiteInfo siteInfo = new SiteInfo();
		MapToBeanUtil.convert(siteInfo, map);
		siteInfoService.save(siteInfo);
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		SiteInfo siteInfo = new SiteInfo();
		MapToBeanUtil.convert(siteInfo, map);
		siteInfoService.update(siteInfo);
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		resultData.setData("site",siteInfoService.findById(id));
		return resultData;
	}
	
	@RequestMapping(value = "/delete")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object delete(@RequestBody Map<String,String> map) {
		ResultData resultData = new ResultData();
		siteInfoService.deleteById(map.get("id"));
		return resultData;
	}
}
