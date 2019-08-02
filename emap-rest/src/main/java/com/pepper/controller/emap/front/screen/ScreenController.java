package com.pepper.controller.emap.front.screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.pepper.model.emap.screen.ScreenMap;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.model.emap.vo.ScreenVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.building.BuildingInfoService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.map.MapImageUrlService;
import com.pepper.service.emap.map.MapService;
import com.pepper.service.emap.screen.ScreenMapService;
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
	
	@Reference
	private ScreenMapService screenMapService;
	
	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String buildingId,String siteId) {
		Pager<Screen> pager = new Pager<Screen>();	
		pager = screenService.findNavigator(pager);
		if(StringUtils.hasText(buildingId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_buildingId", buildingId);
		}
		if(StringUtils.hasText(siteId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_siteId", siteId);
		}
		
		List<Screen> list = pager.getResults();
		List<ScreenVo> returnList = new ArrayList<ScreenVo>();
		for(Screen screen : list) {
			returnList.add(convertScreenVo(screen));
		}
		pager.setData("screen",returnList);
		pager.setResults(null);
		systemLogService.log("get screen list", this.request.getRequestURL().toString());
		return pager;
	}
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody String str) throws IOException {
		ResultData resultData = new ResultData();
		Screen screen = new Screen();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(str);
		screen.setBuildingId(jsonNode.get("buildingId").asText(""));
		screen.setSiteId(jsonNode.get("siteId").asText(""));
		screen.setRefreshFrequency(jsonNode.get("refreshFrequency").asInt(0));
		screen = screenService.save(screen);
		
		Iterator<JsonNode> map = jsonNode.get("map").iterator();
		while(map.hasNext()) {
			JsonNode node = map.next();
			ScreenMap screenMap = new ScreenMap();
			screenMap.setScreenId(screen.getId());
			screenMap.setMapId(node.asText());
			screenMapService.save(screenMap);
		}
		systemLogService.log("screen add", this.request.getRequestURL().toString());
		return resultData;
	}
	
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody String str) throws IOException {
		ResultData resultData = new ResultData();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(str);
		Screen screen = screenService.findById(jsonNode.get("id").asText());
		screen.setBuildingId(jsonNode.get("buildingId").asText(""));
		screen.setSiteId(jsonNode.get("siteId").asText(""));
		screen.setRefreshFrequency(jsonNode.get("refreshFrequency").asInt(0));
		screenService.update(screen);
		
		if(jsonNode.has("map")) {
			screenMapService.deleteByScreenId(screen.getId());
			Iterator<JsonNode> map = jsonNode.get("map").iterator();
			while(map.hasNext()) {
				JsonNode node = map.next();
				ScreenMap screenMap = new ScreenMap();
				screenMap.setScreenId(screen.getId());
				screenMap.setMapId(node.asText());
				screenMapService.save(screenMap);
			}
		}
		systemLogService.log("screen update", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		Screen screen = screenService.findById(id);
		resultData.setData("screen",convertScreenVo(screen));
		systemLogService.log("get screen info", this.request.getRequestURL().toString());
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
				screenMapService.deleteByScreenId(id);
			}catch (Exception e) {
			}
		}
		systemLogService.log("screen delete", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private ScreenVo convertScreenVo(Screen screen) {
		ScreenVo screenVo = new ScreenVo();
		BeanUtils.copyProperties(screen, screenVo);
		
		BuildingInfo build = buildingInfoService.findById(screen.getBuildingId());
		screenVo.setBuild(build);
		
		SiteInfo site = siteInfoService.findById(screen.getSiteId());
		screenVo.setSite(site);
		
		List<ScreenMap> listScreenMap=  screenMapService.findByScreenId(screen.getId());
		List<MapVo> listMapVo = new ArrayList<MapVo>();
		for(ScreenMap screenMap :listScreenMap  ) {
			com.pepper.model.emap.map.Map map = mapService.findById(screenMap.getMapId());
			MapVo mapVo = new MapVo();
			if(map!=null) {
				BeanUtils.copyProperties(map, mapVo);
				mapVo.setMapImageUrl(mapImageUrlService.findByMapId(map.getId()));
				listMapVo.add(mapVo);
			}
		}
		screenVo.setMap(listMapVo);
		return  screenVo;
	}
}
