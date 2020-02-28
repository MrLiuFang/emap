package com.pepper.controller.emap.front.map;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletOutputStream;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.ExcelColumn;
import com.pepper.controller.emap.util.ExportExcelUtil;
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
	
	@RequestMapping(value = "/export")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public void export(String code,String name,String areaCode,String areaName,String buildId,String keyWord,String siteId) throws IOException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		systemLogService.log("map export", this.request.getRequestURL().toString());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xlsx");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("map.xlsx", "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		Pager<com.pepper.model.emap.map.Map> pager = getPager(code, name, areaCode, areaName,buildId,keyWord,siteId,true);
		List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
		excelColumn.add(ExcelColumn.build("code", "code"));
		excelColumn.add(ExcelColumn.build("name", "name"));
		excelColumn.add(ExcelColumn.build("areaCode", "areaCode"));
		excelColumn.add(ExcelColumn.build("areaName", "areaName"));
		excelColumn.add(ExcelColumn.build("buildCode", "build.code"));
		excelColumn.add(ExcelColumn.build("floor", "floor"));
		new ExportExcelUtil().export((Collection<?>) pager.getData().get("map"), outputStream, excelColumn);
	}
	
	@RequestMapping(value = "/import")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importStaff(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		List<com.pepper.model.emap.map.Map> list = new ArrayList<com.pepper.model.emap.map.Map>();
		for (String fileName : files.keySet()) {
			MultipartFile file = files.get(fileName);
			Workbook wookbook = null;
	        try {
	        	if(isExcel2003(fileName)){
	        		wookbook = new HSSFWorkbook(file.getInputStream());
	        	}else if(isExcel2007(fileName)){
	        		wookbook = new XSSFWorkbook(file.getInputStream());
	        	}
	        } catch (IOException e) {
	        }
	        
	        Sheet sheet = wookbook.getSheetAt(0);
	        Row rowHead = sheet.getRow(0);
			int totalRowNum = sheet.getLastRowNum();
			if(!check(sheet.getRow(0))) {
				resultData.setMessage("数据错误！");
				return resultData;
			}
			for(int i = 1 ; i <= totalRowNum ; i++)
	        {
				Row row = sheet.getRow(i);
				com.pepper.model.emap.map.Map map = new com.pepper.model.emap.map.Map();
				map.setCode(getCellValue(row.getCell(0)).toString());
				map.setName(getCellValue(row.getCell(1)).toString());
				map.setAreaCode(getCellValue(row.getCell(2)).toString());
				map.setAreaName(getCellValue(row.getCell(3)).toString());
				map.setFloor(getCellValue(row.getCell(5)).toString());
				String build = getCellValue(row.getCell(4)).toString();
				if (StringUtils.hasText(map.getCode())&&mapService.findByCode(map.getCode()) == null) {
					BuildingInfo buildingInfo = this.buildingInfoService.findByCode(build);
					if(buildingInfo!=null) {
						map.setBuildId(buildingInfo.getId());
//						list.add(map);
					}else {
						continue;
					}
				}
				
				if (StringUtils.hasText(map.getCode())) {
					com.pepper.model.emap.map.Map oldMap = mapService.findByCode(map.getCode());
					if(Objects.nonNull(oldMap)) {
						String isDelete = getCellValue(row.getCell(6)).toString();
						if(Objects.equals(isDelete.trim(), "是")) {
							mapService.deleteById(oldMap.getId());
							continue;
						}else {
							map.setId(oldMap.getId());
							mapService.update(map);
							continue;
						}
					}
					list.add(map);
				}
	        }
			this.mapService.saveAll(list);
		}
//		systemLogService.log("import map");
		return resultData;
	}
	
	private  boolean isExcel2003(String filePath){
        return StringUtils.hasText(filePath) && filePath.endsWith(".xls");
    }
	private  boolean isExcel2007(String filePath){
        return StringUtils.hasText(filePath) && filePath.endsWith(".xlsx");
    }
	
	private Boolean check(Row row) {
		if(!getCellValue(row.getCell(0)).toString().equals("code")) {
			return false;
		}
		if(!getCellValue(row.getCell(1)).toString().equals("name")) {
			return false;
		}
		if(!getCellValue(row.getCell(2)).toString().equals("areaCode")) {
			return false;
		}
		if(!getCellValue(row.getCell(3)).toString().equals("areaName")) {
			return false;
		}
		if(!getCellValue(row.getCell(4)).toString().equals("buildCode")) {
			return false;
		}
		if(!getCellValue(row.getCell(5)).toString().equals("floor")) {
			return false;
		}
		if(!getCellValue(row.getCell(6)).toString().equals("isDelete")) {
			return false;
		}
		
		return true;
	}
	
	private Object getCellValue(Cell cell) {
		if(cell == null) {
			return "";
		}
		Object object = "";
		switch (cell.getCellType()) {
		case STRING :
			object = cell.getStringCellValue();
			break;
		case NUMERIC :
			object = cell.getNumericCellValue();
			break;
		case BOOLEAN :
			object = cell.getBooleanCellValue();
			break;
		default:
			break;
		}
		return object;
	}
	
	private Pager<com.pepper.model.emap.map.Map> getPager(String code,String name,String areaCode,String areaName,String buildId,String keyWord,String siteId, Boolean isExport) {
		Pager<com.pepper.model.emap.map.Map> pager = new Pager<com.pepper.model.emap.map.Map>();
		if (Objects.equals(isExport, true)) {
			pager.setPageNo(1);
			pager.setPageSize(Integer.MAX_VALUE);
		}
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
		if(StringUtils.hasText(keyWord)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.OR_LIKE+"_name&code&areaName&areaCode",keyWord );
//			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",keyWord );
		}
		pager = mapService.findNavigator(pager);
		
		List<com.pepper.model.emap.map.Map> list = pager.getResults();
		List<MapVo> returnList = new ArrayList<MapVo>();
		for(com.pepper.model.emap.map.Map entity : list) {
			
			returnList.add(convertMapVo(entity));
		}
		pager.setData("map",returnList);
		pager.setResults(null);
		return pager;
	}

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
	public Object list(String code,String name,String areaCode,String areaName,String buildId,String keyWord,String siteId) {
		
//		systemLogService.log("get map list", this.request.getRequestURL().toString());
		return getPager(code, name, areaCode, areaName,buildId,keyWord,siteId,false);
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
