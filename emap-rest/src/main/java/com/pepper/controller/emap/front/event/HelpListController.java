package com.pepper.controller.emap.front.event;

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
import org.springframework.data.domain.Sort.Direction;
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
import com.pepper.model.emap.event.HelpList;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.screen.Screen;
import com.pepper.model.emap.vo.HelpListVo;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.model.emap.vo.NodeVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.event.HelpListService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.node.NodeTypeService;
import com.pepper.util.MapToBeanUtil;

@Controller
@RequestMapping(value = "/front/help")
@Validated
public class HelpListController extends BaseControllerImpl implements BaseController {

	@Reference
	private HelpListService helpListService;
	
	@Reference
	private NodeTypeService nodeTypeService;
	
	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping(value = "/export")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public void export(String code,String name,String nodeTypeId,Integer warningLevel,String keyWord) throws IOException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		systemLogService.log("help export", this.request.getRequestURL().toString());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xlsx");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("help.xlsx", "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		Pager<HelpList> pager = getPager(code, name, nodeTypeId, warningLevel, keyWord, true);
		List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
		excelColumn.add(ExcelColumn.build("code", "code"));
		excelColumn.add(ExcelColumn.build("name", "name"));
		excelColumn.add(ExcelColumn.build("nodeType", "nodeType.code"));
		excelColumn.add(ExcelColumn.build("warningLevel", "warningLevel"));
		excelColumn.add(ExcelColumn.build("helpMessage", "helpMessage"));
		
		new ExportExcelUtil().export((Collection<?>) pager.getData().get("help"), outputStream, excelColumn);
	}
	
	private Pager<HelpList> getPager(String code,String name,String nodeTypeId,Integer warningLevel,String keyWord, Boolean isExport) {
		Pager<HelpList> pager = new Pager<HelpList>();
		if (Objects.equals(isExport, true)) {
			pager.setPageNo(1);
			pager.setPageSize(Integer.MAX_VALUE);
		}
		if(StringUtils.hasText(code)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_code",code);
		}
		if(StringUtils.hasText(name)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name);
		}
		if(StringUtils.hasText(nodeTypeId)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_nodeTypeId",nodeTypeId);
		}
		if(warningLevel!=null) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_warningLevel",warningLevel);
		}
		
		if(StringUtils.hasText(keyWord)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.OR_LIKE+"_name&code",keyWord );
		}
		pager.getJpqlParameter().setSortParameter("code",Direction.ASC);
		
		pager = helpListService.findNavigator(pager);
		List<HelpList> list = pager.getResults();
		List<HelpListVo> returnList = new ArrayList<HelpListVo>();
		for(HelpList helpList : list ) {
			returnList.add(this.convertHelpList(helpList));
		}
		pager.setData("help",returnList);
		pager.setResults(null);
		return pager;
	}
	
	@RequestMapping(value = "/import")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importStaff(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		List<HelpList> list = new ArrayList<HelpList>();
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
				HelpList helpList= new HelpList();
				helpList.setCode(getCellValue(row.getCell(0)).toString());
				helpList.setName(getCellValue(row.getCell(1)).toString());
				helpList.setWarningLevel(Integer.valueOf(row.getCell(3).toString().replaceAll("(\\.(\\d*))", "")));
				helpList.setHelpMessage(getCellValue(row.getCell(4)).toString());
				NodeType nodeType = this.nodeTypeService.findByCode(getCellValue(row.getCell(2)).toString());
				if(nodeType!=null) {
					helpList.setNodeTypeId(nodeType.getId());
				}else {
					continue;
				}
				if (StringUtils.hasText(helpList.getCode())) {
					HelpList oldHelpList = helpListService.findByCode(helpList.getCode());
					if(Objects.nonNull(oldHelpList)) {
						String isDelete = getCellValue(row.getCell(5)).toString();
						if(Objects.equals(isDelete.trim(), "是")) {
							helpListService.deleteById(oldHelpList.getId());
							continue;
						}else {
							helpList.setId(oldHelpList.getId());
							
							helpListService.update(helpList);
							continue;
						}
					}
					list.add(helpList);
				}
	        }
			this.helpListService.saveAll(list);
		}
//		systemLogService.log("import help list");
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
		if(!getCellValue(row.getCell(2)).toString().equals("nodeType")) {
			return false;
		}
		if(!getCellValue(row.getCell(3)).toString().equals("warningLevel")) {
			return false;
		}
		if(!getCellValue(row.getCell(4)).toString().equals("helpMessage")) {
			return false;
		}
		if(!getCellValue(row.getCell(5)).toString().equals("isDelete")) {
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
	public Object list(String code,String name,String nodeTypeId,Integer warningLevel,String keyWord) {
		
//		systemLogService.log("get help list", this.request.getRequestURL().toString());
		return getPager(code, name, nodeTypeId, warningLevel, keyWord, false);
	}
	
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		HelpList helpList = new HelpList();
		MapToBeanUtil.convert(helpList, map);
		if(helpListService.findByCode(helpList.getCode())!=null) {
			resultData.setCode(2000001);
			resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
			return resultData;
		}
		helpListService.save(helpList);
		systemLogService.log("help list add", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		HelpList helpList = new HelpList();
		MapToBeanUtil.convert(helpList, map);
		HelpList oldHelpList = helpListService.findByCode(helpList.getCode());
		if(oldHelpList!=null && oldHelpList.getCode()!=null&&helpList.getCode()!=null) {
			if(!helpList.getId().equals(oldHelpList.getId())){
				resultData.setCode(2000001);
				resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
				return resultData;
			}
		}
		helpListService.update(helpList);
		systemLogService.log("help list update", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		HelpList helpList = helpListService.findById(id);
		if(helpList!=null) {
			resultData.setData("help",this.convertHelpList(helpList));
		}
		systemLogService.log("get help list info", this.request.getRequestURL().toString());
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
				helpListService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("help list delete", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private HelpListVo convertHelpList(HelpList helpList) {
		HelpListVo  helpListVo = new HelpListVo();
		if(helpList!=null) {
			BeanUtils.copyProperties(helpList, helpListVo);
			helpListVo.setNodeType(nodeTypeService.findById(helpList.getNodeTypeId()));
		}
		return helpListVo;
	}
}
