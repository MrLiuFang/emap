package com.pepper.controller.emap.front.screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
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
import com.pepper.model.emap.screen.Screen;
import com.pepper.model.emap.screen.ScreenVo;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.building.BuildingInfoService;
import com.pepper.service.emap.map.MapImageUrlService;
import com.pepper.service.emap.map.MapService;
import com.pepper.service.emap.screen.ScreenService;
import com.pepper.service.emap.site.SiteInfoService;
import com.pepper.util.MapToBeanUtil;

@Controller()
@RequestMapping(value = "/front/screen")
@Validated
public class ScreenController extends BaseControllerImpl implements BaseController {

	@Reference
	private ScreenService screenService;
	
	@Reference
	private BuildingInfoService buildingInfoService;
	
	@Reference
	private SiteInfoService siteInfoService;
	
	@Reference
	private MapService mapService;
	
	@Reference
	private MapImageUrlService mapImageUrlService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String buildingId,String siteId,String mapId,Integer sort) {
		Pager<Screen> pager = new Pager<Screen>();	
		pager = screenService.findNavigator(pager);
		if(StringUtils.hasText(buildingId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_buildingId", buildingId);
		}
		if(StringUtils.hasText(siteId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_siteId", siteId);
		}
		if(StringUtils.hasText(mapId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_mapId", mapId);
		}
		if(sort!=null) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_sort", sort);
		}
		List<Screen> list = pager.getResults();
		List<ScreenVo> returnList = new ArrayList<ScreenVo>();
		for(Screen screen : list) {
			returnList.add(convertScreenVo(screen));
		}
		pager.setData("screen",returnList);
		pager.setResults(null);
		return pager;
	}
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Screen screen = new Screen();
		MapToBeanUtil.convert(screen, map);
		screenService.save(screen);
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Screen screen = new Screen();
		MapToBeanUtil.convert(screen, map);
		screenService.update(screen);
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		Screen screen = screenService.findById(id);
		resultData.setData("node",screen);
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
				screenService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		return resultData;
	}
	
	private ScreenVo convertScreenVo(Screen screen) {
		ScreenVo screenVo = new ScreenVo();
		BeanUtils.copyProperties(screen, screenVo);
		
		BuildingInfo build = buildingInfoService.findById(screen.getBuildingId());
		screenVo.setBuild(build);
		
		SiteInfo site = siteInfoService.findById(screen.getSiteId());
		screenVo.setSite(site);
		
		com.pepper.model.emap.map.Map map = mapService.findById(screen.getMapId());
		MapVo mapVo = new MapVo();
		BeanUtils.copyProperties(map, mapVo);
		mapVo.setMapImageUrl(mapImageUrlService.findByMapId(map.getId()));
		screenVo.setMap(mapVo);
		
		return  screenVo;
	}
}
