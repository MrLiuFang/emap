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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.building.BuildingInfo;
import com.pepper.model.emap.department.Department;
import com.pepper.model.emap.map.MapImageUrl;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.vo.BuildingInfoVo;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.building.BuildingInfoService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.map.MapImageUrlService;
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
	
	@Reference
	private MapImageUrlService mapImageUrlService;
	
	@Reference
	private SystemLogService systemLogService;

	private void addMapImageUrl(String mapId, String data) throws IOException {
		mapImageUrlService.deleteByMapId(mapId);
		JsonNode jsonNode = new ObjectMapper().readTree(data);
		ArrayNode arrayNode =  (ArrayNode) jsonNode;
		for(JsonNode node : arrayNode) {
			MapImageUrl mapImageUrl = new MapImageUrl();
			mapImageUrl.setCode(node.get("code").asText());
			mapImageUrl.setUrl(node.get("url").asText());
			if(node.hasNonNull("ratate")) {
				mapImageUrl.setRatate(node.get("ratate").asDouble());
			}
			if(node.hasNonNull("offsetX")) {
				mapImageUrl.setOffsetX(node.get("offsetX").asDouble());
			}
			if(node.hasNonNull("offsetY")) {
				mapImageUrl.setOffsetY(node.get("offsetY").asDouble());
			}
			mapImageUrl.setMapId(mapId);
			mapImageUrlService.save(mapImageUrl);
		}
	}
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String code,String name,String areaCode,String areaName,String buildId) {
		Pager<com.pepper.model.emap.map.Map> pager = new Pager<com.pepper.model.emap.map.Map>();
		if(StringUtils.hasText(code)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_code",code );
		}
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
		}
		if(StringUtils.hasText(areaCode)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_areaCode",areaCode );
		}
		if(StringUtils.hasText(areaName)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_areaName",areaName );
		}
		
		if(StringUtils.hasText(buildId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_buildId",buildId );
		}
		pager = mapService.findNavigator(pager);
		
		List<com.pepper.model.emap.map.Map> list = pager.getResults();
		List<MapVo> returnList = new ArrayList<MapVo>();
		for(com.pepper.model.emap.map.Map entity : list) {
			
			returnList.add(convertMapVo(entity));
		}
		pager.setData("map",returnList);
		pager.setResults(null);
		systemLogService.log("get map list", this.request.getRequestURL().toString());
		return pager;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody String data) throws JsonParseException, JsonMappingException, IOException {
		ResultData resultData = new ResultData();
		JsonNode jsonNode = new ObjectMapper().readTree(data);
		Map<String,Object> map = new ObjectMapper().readValue(data, Map.class);
		
		com.pepper.model.emap.map.Map entity = new com.pepper.model.emap.map.Map();
		MapToBeanUtil.convert(entity, map);
		if(mapService.findByCode(entity.getCode())!=null) {
			resultData.setCode(2000001);
			resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
			return resultData;
		}
		entity = mapService.save(entity);
		if(jsonNode.hasNonNull("mapImageUrl")) {
			String mapImageUrl = jsonNode.get("mapImageUrl").toString();
			addMapImageUrl(entity.getId(),mapImageUrl);
		}
		systemLogService.log("map add", this.request.getRequestURL().toString());
		return resultData;
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody String data) throws IOException {
		ResultData resultData = new ResultData();
		JsonNode jsonNode = new ObjectMapper().readTree(data);
		Map<String,Object> map = new ObjectMapper().readValue(data, Map.class);
		
		com.pepper.model.emap.map.Map entity = new com.pepper.model.emap.map.Map();
		MapToBeanUtil.convert(entity, map);
		
		com.pepper.model.emap.map.Map oldMap = mapService.findByCode(entity.getCode());
		if(oldMap!=null && oldMap.getCode()!=null&&entity.getCode()!=null) {
			if(!entity.getId().equals(oldMap.getId())){
				resultData.setCode(2000001);
				resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
				return resultData;
			}
		}
		
		
		mapService.update(entity);
		if(jsonNode.hasNonNull("mapImageUrl")) {
			String mapImageUrl = jsonNode.get("mapImageUrl").toString();
			addMapImageUrl(entity.getId(),mapImageUrl);
		}
		systemLogService.log("map update", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		com.pepper.model.emap.map.Map  entity = mapService.findById(id);
		resultData.setData("map",convertMapVo(entity));
		systemLogService.log("get map info", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private MapVo convertMapVo(com.pepper.model.emap.map.Map map) {
		MapVo  mapVo = new MapVo();
		BeanUtils.copyProperties(map, mapVo);
		mapVo.setMapImageUrl(mapImageUrlService.findByMapId(map.getId()));
		BuildingInfo buildingInfo =  buildingInfoService.findById(map.getBuildId());
		BuildingInfoVo buildingInfoVo = new BuildingInfoVo();
		if(buildingInfo!=null) {
			BeanUtils.copyProperties(buildingInfo, buildingInfoVo);
			mapVo.setBuild(buildingInfoVo);
			SiteInfo siteInfo = siteInfoService.findById(buildingInfo.getSiteInfoId());
			buildingInfoVo.setSite(siteInfo);
		}
		
		
		return mapVo;
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
		systemLogService.log("map delete", this.request.getRequestURL().toString());
		return resultData;
	}
}
