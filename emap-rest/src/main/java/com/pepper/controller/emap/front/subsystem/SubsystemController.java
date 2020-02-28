package com.pepper.controller.emap.front.subsystem;

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
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.subsystem.Subsystem;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.subsystem.SubsystemService;
import com.pepper.util.MapToBeanUtil;

@Controller()
@RequestMapping(value = "/front/subsystem")
public class SubsystemController extends BaseControllerImpl implements BaseController{
	
	@Reference
	private SubsystemService subsystemService;

	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping(value = "/export")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public void export(String name,String code,String nodeCode,Boolean isOnLine) throws IOException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		systemLogService.log("help export", this.request.getRequestURL().toString());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xlsx");
		response.setHeader("Content-Disposition",
				"attachment;filename=" + URLEncoder.encode("subsystem.xlsx", "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		Pager<Subsystem> pager = getPager( name,code,nodeCode,isOnLine, true);
		List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
		excelColumn.add(ExcelColumn.build("code", "code"));
		excelColumn.add(ExcelColumn.build("name", "name"));
		excelColumn.add(ExcelColumn.build("address", "address"));
		excelColumn.add(ExcelColumn.build("prot", "prot"));
		excelColumn.add(ExcelColumn.build("isRelation", "isRelation"));
		excelColumn.add(ExcelColumn.build("nodeCode", "nodeCode"));
		new ExportExcelUtil().export((Collection<?>) pager.getData().get("subsystem"), outputStream, excelColumn);
	}

	
	@RequestMapping(value = "/import")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importStaff(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		List<Subsystem> list = new ArrayList<Subsystem>();
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
				Subsystem subsystem= new Subsystem();
				subsystem.setCode(getCellValue(row.getCell(0)).toString());
				subsystem.setName(getCellValue(row.getCell(1)).toString());
				subsystem.setAddress(getCellValue(row.getCell(2)).toString());
				subsystem.setProt(Integer.valueOf( getCellValue(row.getCell(3)).toString().replaceAll("(\\.(\\d*))", "")));
				subsystem.setIsRelation(Objects.equals(getCellValue(row.getCell(4)).toString(), "是"));
				subsystem.setNodeCode(getCellValue(row.getCell(5)).toString());
				if (StringUtils.hasText(subsystem.getCode())) {
					Subsystem oldSubsystem = subsystemService.findByCode(subsystem.getCode());
					if(Objects.nonNull(oldSubsystem)) {
						String isDelete = getCellValue(row.getCell(6)).toString();
						if(Objects.equals(isDelete.trim(), "是")) {
							subsystemService.deleteById(oldSubsystem.getId());
							continue;
						}else {
							subsystem.setId(oldSubsystem.getId());
							subsystemService.update(subsystem);
							continue;
						}
					}
					list.add(subsystem);
				}
				
	        }
			this.subsystemService.saveAll(list);
		}
//		systemLogService.log("import subsystem");
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
		if(!getCellValue(row.getCell(2)).toString().equals("address")) {
			return false;
		}
		if(!getCellValue(row.getCell(3)).toString().equals("prot")) {
			return false;
		}
		if(!getCellValue(row.getCell(4)).toString().equals("isRelation")) {
			return false;
		}
		if(!getCellValue(row.getCell(5)).toString().equals("nodeCode")) {
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
	
	private Pager< Subsystem> getPager(String name,String code,String nodeCode,Boolean isOnLine, Boolean isExport) {
		Pager< Subsystem> pager = new Pager< Subsystem>();
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
		}
		if(StringUtils.hasText(nodeCode)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_nodeCode",nodeCode );
		}
		if(StringUtils.hasText(code)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_code",code );
		}
		if(isOnLine!=null&&isOnLine) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.IS_TRUE+"_isOnLine",isOnLine );
		}
		
		pager = subsystemService.findNavigator(pager);
		pager.setData("subsystem",pager.getResults());
		pager.setResults(null);
		return pager;
	}
	
	
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String name,String code,String nodeCode,Boolean isOnLine) {
//		systemLogService.log("get subsystem list", this.request.getRequestURL().toString());
		return getPager(name,code, nodeCode,isOnLine, false);
	}
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Subsystem subsystem = new Subsystem();
		MapToBeanUtil.convert(subsystem, map);
		if(subsystemService.findByCode(subsystem.getCode())!=null) {
			resultData.setCode(2000001);
			resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
			return resultData;
		}
		subsystem.setIsOnLine(false);
		subsystemService.save(subsystem);
		systemLogService.log("site subsystem", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Subsystem subsystem = new Subsystem();
		MapToBeanUtil.convert(subsystem, map);
		Subsystem oldSubsystem = subsystemService.findByCode(subsystem.getCode());
		if(oldSubsystem!=null && oldSubsystem.getCode()!=null&&subsystem.getCode()!=null) {
			if(!subsystem.getId().equals(oldSubsystem.getId())){
				resultData.setCode(2000001);
				resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
				return resultData;
			}
		}
		subsystemService.update(subsystem);
		systemLogService.log("site subsystem", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		resultData.setData("subsystem",subsystemService.findById(id));
		systemLogService.log("get subsystem info", this.request.getRequestURL().toString());
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
				subsystemService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("subsystem delete", this.request.getRequestURL().toString());
		return resultData;
	}
}
