package com.pepper.controller.emap.front.map;

import java.io.IOException;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.building.BuildingInfo;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.vo.BuildingInfoVo;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.building.BuildingInfoService;
import com.pepper.service.emap.map.MapService;
import com.pepper.service.emap.site.SiteInfoService;
import com.pepper.util.MapToBeanUtil;

@Controller()
@RequestMapping(value = "/front/map")
public class MapController  extends BaseControllerImpl implements BaseController  {
	
	
	@Reference
	private BuildingInfoService buildingInfoService;
	
	@Reference
	private SiteInfoService siteInfoService;
	
	@Reference
	private MapService mapService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String code,String name,String areaCode,String areaName,String buildId,String keyWord) {
		Pager<com.pepper.model.emap.map.Map> pager = new Pager<com.pepper.model.emap.map.Map>();
		if(StringUtils.hasText(code)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_code",code );
		}
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_name",name );
		}
		if(StringUtils.hasText(code)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_areaCode",areaCode );
		}
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_areaName",areaName );
		}
		
		if(StringUtils.hasText(buildId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_buildId",buildId );
		}
		if(StringUtils.hasText(keyWord)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.ORLIKE+"_buildId&code&name&areaCode&areaName",keyWord );
		}
		pager = mapService.findNavigator(pager);
		
		List<com.pepper.model.emap.map.Map> list = pager.getResults();
		List<MapVo> returnList = new ArrayList<MapVo>();
		for(com.pepper.model.emap.map.Map entity : list) {
			MapVo  mapVo = new MapVo();
			BeanUtils.copyProperties(entity, mapVo);
			BuildingInfo buildingInfo =  buildingInfoService.findById(entity.getBuildId());
			BuildingInfoVo buildingInfoVo = new BuildingInfoVo();
			BeanUtils.copyProperties(buildingInfo, buildingInfoVo);
			mapVo.setBuild(buildingInfoVo);
			SiteInfo siteInfo = siteInfoService.findById(buildingInfo.getSiteInfoId());
			buildingInfoVo.setSite(siteInfo);
			returnList.add(mapVo);
		}
		pager.setData("map",returnList);
		pager.setResults(null);
		return pager;
	}

	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		com.pepper.model.emap.map.Map entity = new com.pepper.model.emap.map.Map();
		MapToBeanUtil.convert(entity, map);
		mapService.save(entity);
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		com.pepper.model.emap.map.Map entity = new com.pepper.model.emap.map.Map();
		MapToBeanUtil.convert(entity, map);
		mapService.update(entity);
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		com.pepper.model.emap.map.Map  entity = mapService.findById(id);
		MapVo  mapVo = new MapVo();
		BeanUtils.copyProperties(entity, mapVo);
		BuildingInfo buildingInfo =  buildingInfoService.findById(entity.getBuildId());
		BuildingInfoVo buildingInfoVo = new BuildingInfoVo();
		BeanUtils.copyProperties(buildingInfo, buildingInfoVo);
		mapVo.setBuild(buildingInfoVo);
		SiteInfo siteInfo = siteInfoService.findById(buildingInfo.getSiteInfoId());
		buildingInfoVo.setSite(siteInfo);
		resultData.setData("map",mapVo);
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
		for(int i = 0; i < arrayNode.size(); i++) {
			String id = arrayNode.get(i).asText();
			try {
				mapService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		return resultData;
	}
}
