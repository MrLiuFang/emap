package com.pepper.controller.emap.front.building;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
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
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.console.role.RoleUser;
import com.pepper.model.emap.building.BuildingInfo;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.vo.AdminUserVo;
import com.pepper.model.emap.vo.BuildingInfoVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.building.BuildingInfoService;
import com.pepper.service.emap.site.SiteInfoService;
import com.pepper.util.MapToBeanUtil;

@Controller()
@RequestMapping(value = "/front/build")
public class BuildingController  extends BaseControllerImpl implements BaseController  {
	
	@Reference
	private BuildingInfoService buildingInfoService;
	
	@Reference
	private SiteInfoService siteInfoService;

	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String code,String name) {
		Pager<BuildingInfo> pager = new Pager<BuildingInfo>();
		if(StringUtils.hasText(code)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_code",code );
		}
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
		}
		pager = buildingInfoService.findNavigator(pager);
		
		List<BuildingInfo> list = pager.getResults();
		List<BuildingInfoVo> returnList = new ArrayList<BuildingInfoVo>();
		for(BuildingInfo buildingInfo : list) {
			BuildingInfoVo  buildingInfoVo = new BuildingInfoVo();
			BeanUtils.copyProperties(buildingInfo, buildingInfoVo);
			SiteInfo siteInfo = siteInfoService.findById(buildingInfo.getSiteInfoId());
			buildingInfoVo.setSite(siteInfo);
			returnList.add(buildingInfoVo);
		}
		pager.setData("build",returnList);
		pager.setResults(null);
		return pager;
	}
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		BuildingInfo buildingInfo = new BuildingInfo();
		MapToBeanUtil.convert(buildingInfo, map);
		buildingInfoService.save(buildingInfo);
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		BuildingInfo buildingInfo = new BuildingInfo();
		MapToBeanUtil.convert(buildingInfo, map);
		buildingInfoService.update(buildingInfo);
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		BuildingInfo buildingInfo = buildingInfoService.findById(id);
		BuildingInfoVo  buildingInfoVo = new BuildingInfoVo();
		BeanUtils.copyProperties(buildingInfo, buildingInfoVo);
		buildingInfoVo.setSite(this.siteInfoService.findById(buildingInfo.getSiteInfoId()));
		resultData.setData("build",buildingInfoVo);
		return resultData;
	}
	
	@RequestMapping(value = "/delete")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object delete(@RequestBody Map<String,String> map) {
		ResultData resultData = new ResultData();
		buildingInfoService.deleteById(map.get("id"));
		return resultData;
	}
}
