package com.pepper.controller.emap.front.screen;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.naming.directory.SearchControls;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

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
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.screen.Screen;
import com.pepper.model.emap.screen.ScreenMap;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.staff.Staff;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.model.emap.vo.ScreenMapVo;
import com.pepper.model.emap.vo.ScreenVo;
import com.pepper.model.emap.vo.StaffVo;
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

	@RequestMapping(value = "/export")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public void export(String buildingId, String siteId) throws IOException, IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException, SecurityException {
//		systemLogService.log("screen export", this.request.getRequestURL().toString());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xlsx");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("screen.xlsx", "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		Pager<Screen> pager = getPager(buildingId, siteId, true);
		List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
		excelColumn.add(ExcelColumn.build("code", "code"));
		excelColumn.add(ExcelColumn.build("buildCode", "build.code"));
		excelColumn.add(ExcelColumn.build("siteCode", "site.code"));
		excelColumn.add(ExcelColumn.build("refreshFrequency", "refreshFrequency"));
		new ExportExcelUtil().export((Collection<?>) pager.getData().get("screen"), outputStream, excelColumn);
	}

	private Pager<Screen> getPager(String buildingId, String siteId, Boolean isExport) {
		Pager<Screen> pager = new Pager<Screen>();
		pager = screenService.findNavigator(pager);
		if (Objects.equals(isExport, true)) {
			pager.setPageNo(1);
			pager.setPageSize(Integer.MAX_VALUE);
		}
		if (StringUtils.hasText(buildingId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL + "_buildingId", buildingId);
		}
		if (StringUtils.hasText(siteId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL + "_siteId", siteId);
		}

		List<Screen> list = pager.getResults();
		List<ScreenVo> returnList = new ArrayList<ScreenVo>();
		for (Screen screen : list) {
			returnList.add(convertScreenVo(screen));
		}
		pager.setData("screen", returnList);
		pager.setResults(null);
		return pager;
	}
	
	@RequestMapping(value = "/import")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importStaff(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		List<Screen> list = new ArrayList<Screen>();
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
				Screen screen= new Screen();
				screen.setCode(getCellValue(row.getCell(0)).toString());
				String building = getCellValue(row.getCell(1)).toString();
				String site = getCellValue(row.getCell(2)).toString();
				String refreshFrequency = getCellValue(row.getCell(3)).toString().replaceAll("(\\.(\\d*))", "");
				BuildingInfo buildingInfo = this.buildingInfoService.findByCode(building);
				SiteInfo siteInfo = this.siteInfoService.findByCode(site);
				
				if (buildingInfo!=null && siteInfo!=null) {
					screen.setBuildingId(buildingInfo.getId());
					screen.setSiteId(siteInfo.getId());
					screen.setRefreshFrequency(Integer.valueOf(refreshFrequency));
					
					if (StringUtils.hasText(screen.getCode())) {
						Screen oldScreen = screenService.findByCode(screen.getCode());
						if(Objects.nonNull(oldScreen)) {
							String isDelete = getCellValue(row.getCell(4)).toString();
							if(Objects.equals(isDelete.trim(), "是")) {
								screenService.deleteById(oldScreen.getId());
								continue;
							}else {
								screen.setId(oldScreen.getId());
								
								screenService.update(screen);
								continue;
							}
						}
						list.add(screen);
					}
				}
	        }
			this.screenService.saveAll(list);
		}
//		systemLogService.log("import screen", this.request.getRequestURL().toString());
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
		if(!getCellValue(row.getCell(1)).toString().equals("buildCode")) {
			return false;
		}
		if(!getCellValue(row.getCell(2)).toString().equals("siteCode")) {
			return false;
		}
		if(!getCellValue(row.getCell(3)).toString().equals("refreshFrequency")) {
			return false;
		}
		if(!getCellValue(row.getCell(4)).toString().equals("isDelete")) {
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

	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String buildingId, String siteId) {

//		systemLogService.log("get screen list", this.request.getRequestURL().toString());
		return getPager(buildingId, siteId, false);
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
		screen.setCode(jsonNode.get("code").asText(""));
		screen.setRefreshFrequency(jsonNode.get("refreshFrequency").asInt(0));
		
		if(screenService.findByCode(screen.getCode())!=null) {
			resultData.setCode(2000001);
			resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
			return resultData;
		}
		
		screen = screenService.save(screen);

		Iterator<JsonNode> map = jsonNode.get("map").iterator();
		while (map.hasNext()) {
			JsonNode node = map.next();
			ScreenMap screenMap = new ScreenMap();
			screenMap.setScreenId(screen.getId());
			screenMap.setMapId(node.get("mapId").asText(""));
			screenMap.setRefreshFrequency(Integer.valueOf(node.get("refreshFrequency").asText("10")));
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
		screen.setCode(jsonNode.get("code").asText(""));
		screen.setRefreshFrequency(jsonNode.get("refreshFrequency").asInt(0));
		
		Screen oldScreen = screenService.findByCode(screen.getCode());
		if(oldScreen!=null && oldScreen.getCode()!=null&&screen.getCode()!=null) {
			if(!oldScreen.getId().equals(oldScreen.getId())){
				resultData.setCode(2000001);
				resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
				return resultData;
			}
		}
		
		screenService.update(screen);

		if (jsonNode.has("map")) {
			screenMapService.deleteByScreenId(screen.getId());
			Iterator<JsonNode> map = jsonNode.get("map").iterator();
			while (map.hasNext()) {
				JsonNode node = map.next();
				ScreenMap screenMap = new ScreenMap();
				screenMap.setScreenId(screen.getId());
				screenMap.setMapId(node.get("mapId").asText(""));
				screenMap.setRefreshFrequency(Integer.valueOf(node.get("refreshFrequency").asText("10")));
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
		resultData.setData("screen", convertScreenVo(screen));
		systemLogService.log("get screen info", this.request.getRequestURL().toString());
		return resultData;
	}

	@RequestMapping(value = "/delete")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object delete(@RequestBody String str) throws IOException {
		ResultData resultData = new ResultData();
		if (!StringUtils.hasText(str)) {
			return resultData;
		}
		JsonNode jsonNode = new ObjectMapper().readTree(str);
		if (!jsonNode.has("id")) {
			return resultData;
		}
		ArrayNode arrayNode = (ArrayNode) jsonNode.get("id");
		for (int i = 0; i < arrayNode.size(); i++) {
			String id = arrayNode.get(i).asText();
			try {
				screenService.deleteById(id);
				screenMapService.deleteByScreenId(id);
			} catch (Exception e) {
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

		List<ScreenMap> listScreenMap = screenMapService.findByScreenId(screen.getId());
		List<ScreenMapVo> listMapVo = new ArrayList<ScreenMapVo>();
		for (ScreenMap screenMap : listScreenMap) {
			ScreenMapVo screenMapVo = new ScreenMapVo();
			BeanUtils.copyProperties(screenMap, screenMapVo);
			com.pepper.model.emap.map.Map map = mapService.findById(screenMap.getMapId());
			MapVo mapVo = new MapVo();
			if (map != null) {
				BeanUtils.copyProperties(map, mapVo);
				mapVo.setMapImageUrl(mapImageUrlService.findByMapId(map.getId()));
				screenMapVo.setMap(mapVo);
				listMapVo.add(screenMapVo);
			}
		}
		screenVo.setScreenMap(listMapVo);
		return screenVo;
	}
}
