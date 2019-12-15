package com.pepper.controller.emap.front.node;

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
import com.pepper.model.emap.enums.Classify;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.model.emap.vo.NodeTypeVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.node.NodeTypeService;
import com.pepper.service.file.FileService;
import com.pepper.util.MapToBeanUtil;

@Controller()
@RequestMapping(value = "/front/nodeType")
public class NodeTypeController extends BaseControllerImpl implements BaseController {

	@Reference
	private NodeTypeService nodeTypeService;
	
	@Reference
	private FileService fileService;
	
	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping(value = "/export")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public void export(String code,String name,String areaCode,String areaName,String buildId,String keyWord,String siteId,Classify classify) throws IOException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		systemLogService.log("nodeType export", this.request.getRequestURL().toString());
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xlsx");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("nodeType.xlsx", "UTF-8"));
		ServletOutputStream outputStream = response.getOutputStream();
		Pager<NodeType> pager = getPager(code, name, keyWord, classify,true);
		List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
		excelColumn.add(ExcelColumn.build("編碼", "code"));
		excelColumn.add(ExcelColumn.build("名稱", "name"));
		excelColumn.add(ExcelColumn.build("类别", "classify"));
		new ExportExcelUtil().export((Collection<?>) pager.getData().get("nodeType"), outputStream, excelColumn);
	}
	
	private Pager<NodeType> getPager(String code,String name,String keyWord,Classify classify, Boolean isExport) {
		Pager<NodeType> pager = new Pager<NodeType>();
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
		if(Objects.nonNull(classify)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_classify",classify );
		}
		if(StringUtils.hasText(keyWord)) {
			pager.getJpqlParameter().setSearchParameter(SearchConstant.OR_LIKE+"_code&name",keyWord );
		}
		
		pager.getJpqlParameter().setSortParameter("code", Direction.DESC);
		
		pager = nodeTypeService.findNavigator(pager);
		List<NodeType> list = pager.getResults();
		List<NodeTypeVo> returnList = new ArrayList<NodeTypeVo>();
		for(NodeType nodeType : list) {
			returnList.add(convertNodeTypeVo(nodeType));
		}
		
		pager.setData("nodeType",returnList);
		pager.setResults(null);
		return pager;
	}

	@RequestMapping(value = "/import")
//	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importStaff(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		List<NodeType> list = new ArrayList<NodeType>();
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
				NodeType nodeType= new NodeType();
				nodeType.setCode(getCellValue(row.getCell(0)).toString());
				nodeType.setName(getCellValue(row.getCell(1)).toString());
				String classify = getCellValue(row.getCell(2)).toString();
				try {
					nodeType.setClassify(Classify.valueOf(classify));
				}catch (Exception e) {
					nodeType.setClassify(Classify.OTHER);
				}
				if (StringUtils.hasText(nodeType.getCode())) {
					NodeType oldNodeType = nodeTypeService.findByCode(nodeType.getCode());
					if(Objects.nonNull(oldNodeType)) {
						String isDelete = getCellValue(row.getCell(3)).toString();
						if(Objects.equals(isDelete.trim(), "是")) {
							nodeTypeService.deleteById(oldNodeType.getId());
							continue;
						}else {
							nodeType.setId(oldNodeType.getId());
							nodeTypeService.update(nodeType);
							continue;
						}
					}
					list.add(nodeType);
				}
				
	        }
			this.nodeTypeService.saveAll(list);
		}
//		systemLogService.log("import nodeType");
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
		if(!getCellValue(row.getCell(2)).toString().equals("classify")) {
			return false;
		}
		
		if(!getCellValue(row.getCell(3)).toString().equals("isDelete")) {
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
	public Object list(String code,String name,String keyWord,Classify classify) {
		
//		systemLogService.log("get node type list", this.request.getRequestURL().toString());
		return getPager(code, name, keyWord,classify, false);
	}
	
	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		NodeType nodeType = new NodeType();
		MapToBeanUtil.convert(nodeType, map);
		if(map.containsKey("classify")) {
			nodeType.setClassify(Classify.valueOf(map.get("classify").toString()));
		}else {
			nodeType.setClassify(Classify.OTHER);
		}
		if(nodeTypeService.findByCode(nodeType.getCode())!=null) {
			resultData.setCode(2000001);
			resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
			return resultData;
		}
		nodeTypeService.save(nodeType);
		systemLogService.log("node type add", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		NodeType nodeType = new NodeType();
		MapToBeanUtil.convert(nodeType, map);
		if(map.containsKey("classify")) {
			nodeType.setClassify(Classify.valueOf(map.get("classify").toString()));
		}else {
			nodeType.setClassify(Classify.OTHER);
		}
		NodeType oldNodeType = nodeTypeService.findByCode(nodeType.getCode());
		if(oldNodeType!=null && oldNodeType.getCode()!=null&&nodeType.getCode()!=null) {
			if(!nodeType.getId().equals(oldNodeType.getId())){
				resultData.setCode(2000001);
				resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
				return resultData;
			}
		}
		
		nodeTypeService.update(nodeType);
		systemLogService.log("node type update", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		resultData.setData("nodeType",convertNodeTypeVo(nodeTypeService.findById(id)));
//		systemLogService.log("get node type info", this.request.getRequestURL().toString());
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
				nodeTypeService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		systemLogService.log("node type delete", this.request.getRequestURL().toString());
		return resultData;
	}
	
	/**
	 * 
	 * @param nodeType
	 * @return
	 */
	private NodeTypeVo convertNodeTypeVo(NodeType nodeType) {
		if(nodeType == null) {
			return null;
		}
		NodeTypeVo  nodeTypeVo = new NodeTypeVo();
		BeanUtils.copyProperties(nodeType, nodeTypeVo);
		nodeTypeVo.setWorkingIconUrl(fileService.getUrl(nodeType.getWorkingIcon()));
		nodeTypeVo.setProcessingIconUrl(fileService.getUrl(nodeType.getProcessingIcon()));
		nodeTypeVo.setStopIconUrl(fileService.getUrl(nodeType.getStopIcon()));
		return nodeTypeVo;
	}

}
