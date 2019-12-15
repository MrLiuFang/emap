package com.pepper.controller.emap.front.building;

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
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.staff.Staff;
import com.pepper.model.emap.vo.BuildingInfoVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.building.BuildingInfoService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.site.SiteInfoService;
import com.pepper.util.MapToBeanUtil;

@Controller()
@RequestMapping(value = "/front/build")
public class BuildingController extends BaseControllerImpl implements BaseController {

	@Reference
	private BuildingInfoService buildingInfoService;

	@Reference
	private SiteInfoService siteInfoService;

	@Reference
	private SystemLogService systemLogService;

	@RequestMapping(value = "/export")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public void export(String code, String name, String siteId, String keyWord) throws IOException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		systemLogService.log("get build export", this.request.getRequestURL().toString());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xlsx");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("build.xlsx", "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		Pager<BuildingInfo> pager = getPager(code, name, siteId, keyWord, true);
		List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
		excelColumn.add(ExcelColumn.build("編碼", "code"));
		excelColumn.add(ExcelColumn.build("名稱", "name"));
		excelColumn.add(ExcelColumn.build("城區", "site.code"));
		new ExportExcelUtil().export((Collection<?>) pager.getData().get("build"), outputStream, excelColumn);
	}
	
	@RequestMapping(value = "/import")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importBuild(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		List<BuildingInfo> list = new ArrayList<BuildingInfo>();
		for (String fileName : files.keySet()) {
			System.out.println(fileName);
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
				BuildingInfo buildingInfo = new BuildingInfo();
				buildingInfo.setCode(getCellValue(row.getCell(0)).toString());
				buildingInfo.setName(getCellValue(row.getCell(1)).toString());
				
				String site = getCellValue(row.getCell(2)).toString();
				SiteInfo siteInfo = this.siteInfoService.findSiteInfo(site);
				if(siteInfo!=null) {
					buildingInfo.setSiteInfoId(siteInfo.getId());
				}
				
				if (StringUtils.hasText(buildingInfo.getCode())) {
					BuildingInfo oldBuildingInfo = buildingInfoService.findByCode(buildingInfo.getCode());
					if(Objects.nonNull(oldBuildingInfo)) {
						String isDelete = getCellValue(row.getCell(3)).toString();
						if(Objects.equals(isDelete.trim(), "是")) {
							buildingInfoService.deleteById(oldBuildingInfo.getId());
							continue;
						}else {
							buildingInfo.setId(oldBuildingInfo.getId());
							buildingInfoService.update(buildingInfo);
							continue;
						}
					}
					list.add(buildingInfo);
				}
	        }
			
			this.buildingInfoService.saveAll(list);
		}
		systemLogService.log("import buildingInfo");
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
		if(!getCellValue(row.getCell(2)).toString().equals("siteCode")) {
			return false;
		}
		if(!getCellValue(row.getCell(3)).toString().equals("isDelete")) {
			return false;
		}
//		if(!getCellValue(row.getCell(3)).toString().equals("longitude")) {
//			return false;
//		}
//		if(!getCellValue(row.getCell(4)).toString().equals("latitude")) {
//			return false;
//		}
		
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
	public Object list(String code, String name, String siteId, String keyWord) {
//		systemLogService.log("get build list", this.request.getRequestURL().toString());
		return getPager(code, name, siteId, keyWord, false);
	}

	private Pager<BuildingInfo> getPager(String code, String name, String siteId, String keyWord, Boolean isExport) {
		Pager<BuildingInfo> pager = new Pager<BuildingInfo>();
		if (StringUtils.hasText(code)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE + "_code", code);
		}
		if (StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE + "_name", name);
		}
		if (StringUtils.hasText(siteId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE + "_siteInfoId", siteId);
		}
		if (StringUtils.hasText(keyWord)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.OR_LIKE + "_siteInfoId&code&name", keyWord);
		}
		if (Objects.equals(isExport, true)) {
			pager.setPageNo(1);
			pager.setPageSize(Integer.MAX_VALUE);
		}
		pager = buildingInfoService.findNavigator(pager);

		List<BuildingInfo> list = pager.getResults();
		List<BuildingInfoVo> returnList = new ArrayList<BuildingInfoVo>();
		for (BuildingInfo buildingInfo : list) {
			BuildingInfoVo buildingInfoVo = new BuildingInfoVo();
			BeanUtils.copyProperties(buildingInfo, buildingInfoVo);
			SiteInfo siteInfo = siteInfoService.findById(buildingInfo.getSiteInfoId());
			buildingInfoVo.setSite(siteInfo);
			returnList.add(buildingInfoVo);
		}
		pager.setData("build", returnList);
		pager.setResults(null);
		return pager;
	}

	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String, Object> map) {
		ResultData resultData = new ResultData();
		BuildingInfo buildingInfo = new BuildingInfo();
		MapToBeanUtil.convert(buildingInfo, map);
		if (buildingInfoService.findByCode(buildingInfo.getCode()) != null) {
			resultData.setCode(2000001);
			resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
			return resultData;
		}
		buildingInfoService.save(buildingInfo);
		systemLogService.log("build add", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String, Object> map) {
		ResultData resultData = new ResultData();
		BuildingInfo buildingInfo = new BuildingInfo();
		MapToBeanUtil.convert(buildingInfo, map);

		BuildingInfo oldBuildingInfo = buildingInfoService.findByCode(buildingInfo.getCode());
		if (oldBuildingInfo != null && oldBuildingInfo.getCode() != null && buildingInfo.getCode() != null) {
			if (!buildingInfo.getId().equals(oldBuildingInfo.getId())) {
				resultData.setCode(2000001);
				resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
				return resultData;
			}
		}

		buildingInfoService.update(buildingInfo);
		systemLogService.log("build update", this.request.getRequestURL().toString());
		return resultData;
	}

	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		BuildingInfo buildingInfo = buildingInfoService.findById(id);
		BuildingInfoVo buildingInfoVo = new BuildingInfoVo();
		BeanUtils.copyProperties(buildingInfo, buildingInfoVo);
		buildingInfoVo.setSite(this.siteInfoService.findById(buildingInfo.getSiteInfoId()));
		resultData.setData("build", buildingInfoVo);

//		systemLogService.log("get build info ", this.request.getRequestURL().toString());
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
				buildingInfoService.deleteById(id);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("delete build ", this.request.getRequestURL().toString());
		return resultData;
	}

}
