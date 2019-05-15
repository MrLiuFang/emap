package com.pepper.controller.emap.front.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import com.pepper.common.emuns.Status;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.building.BuildingInfo;
import com.pepper.model.emap.node.Node;
import com.pepper.model.emap.node.NodeType;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.model.emap.vo.BuildingInfoVo;
import com.pepper.model.emap.vo.MapVo;
import com.pepper.model.emap.vo.NodeTypeVo;
import com.pepper.model.emap.vo.NodeVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.building.BuildingInfoService;
import com.pepper.service.emap.map.MapImageUrlService;
import com.pepper.service.emap.map.MapService;
import com.pepper.service.emap.node.NodeService;
import com.pepper.service.emap.node.NodeTypeService;
import com.pepper.service.emap.site.SiteInfoService;
import com.pepper.service.file.FileService;
import com.pepper.util.MapToBeanUtil;

/**
 * 
 * @author Mr.Liu
 *
 */
@Controller()
@RequestMapping(value = "/front/node")
public class NodeController extends BaseControllerImpl  implements BaseController {
	
	@Reference
	private NodeService nodeService;
	
	@Reference
	private NodeTypeService nodeTypeService;
	
	@Reference
	private FileService fileService;
	
	@Reference
	private MapService mapService;
	
	@Reference
	private BuildingInfoService buildingInfoService;
	
	@Reference
	private SiteInfoService siteInfoService;

	@Reference
	private MapImageUrlService mapImageUrlService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String code,String name,String source,String sourceCode,String mapId,String nodeTypeId,String siteId,String buildId,String floor) {
		Pager<Node> pager = new Pager<Node>();	
		pager = nodeService.findNavigator(pager,code,name,source,sourceCode,mapId,nodeTypeId,siteId,buildId,floor);
		List<Node> list = pager.getResults();
		List<NodeVo> returnList = new ArrayList<NodeVo>();
		for(Node node : list) {
			returnList.add(convertNodeVo(node));
		}
		pager.setData("node",returnList);
		pager.setResults(null);
		return pager;
	}
	
	

	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Node node = new Node();
		MapToBeanUtil.convert(node, map);
		nodeService.save(node);
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Node node = new Node();
		MapToBeanUtil.convert(node, map);
		nodeService.update(node);
		return resultData;
	}
	
	@RequestMapping(value = "/updateStatus")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object updateStatus(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Node node = nodeService.findById(map.get("id").toString());
		node.setStatus(Status.valueOf(map.get("status").toString().toUpperCase()));
		nodeService.update(node);
		return resultData;
	}
	
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		Node node = nodeService.findById(id);
		resultData.setData("node",convertNodeVo(node));
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
				nodeService.deleteById(id);
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		return resultData;
	}
	
	@RequestMapping(value = "/importCamera")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importCamera(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		for (String fileName : files.keySet()) {
			MultipartFile file = files.get(fileName);
			importNode(file.getInputStream(),"camera");
		}
		return resultData;
	}
	
	@RequestMapping(value = "/importDoor")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importDoor(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		for (String fileName : files.keySet()) {
			MultipartFile file = files.get(fileName);
			importNode(file.getInputStream(),"door");
		}
		return resultData;
	}
	
	private void importNode(InputStream inputStream, String nodeType) throws IOException {
		if (inputStream == null) {
			return;
		}
		Workbook wookbook = new HSSFWorkbook(inputStream);
        Sheet sheet = wookbook.getSheetAt(0);
        Row rowHead = sheet.getRow(0);
		int totalRowNum = sheet.getLastRowNum();
		for(int i = 1 ; i <= totalRowNum ; i++)
        {
			Node node = new Node();
			node.setStatus(Status.NORMAL);
			Row row = sheet.getRow(i);
			node.setCode(getCellValue(row.getCell(0)).toString());
			node.setName(getCellValue(row.getCell(1)).toString());
			node.setSource(getCellValue(row.getCell(2)).toString());
			node.setSourceCode(getCellValue(row.getCell(3)).toString());
			node.setMapId(getCellValue(row.getCell(4)).toString());
			node.setNodeTypeId(getCellValue(row.getCell(5)).toString());
			node.setParentNode(getCellValue(row.getCell(6)).toString());
			node.setX(getCellValue(row.getCell(7)).toString());
			node.setY(getCellValue(row.getCell(8)).toString());
			if(nodeType.equals("camera")) {
				node.setIp(getCellValue(row.getCell(9)).toString());
				node.setHasPtz(row.getCell(10)==null?false:Boolean.valueOf(getCellValue(row.getCell(10)).toString()));
				node.setUserName(getCellValue(row.getCell(11)).toString());
				node.setPassword(getCellValue(row.getCell(12)).toString());
				node.setSystemID(getCellValue(row.getCell(13)).toString());
				node.setWindowsUser(getCellValue(row.getCell(14)).toString());
				node.setWindowsPass(getCellValue(row.getCell(15)).toString());
				node.setDomainName(getCellValue(row.getCell(16)).toString());
			}else if(nodeType.equals("door")) {
				node.setPaneId(getCellValue(row.getCell(9)).toString());
				node.setPaneIp(getCellValue(row.getCell(10)).toString());
				node.setReaderId(getCellValue(row.getCell(11)).toString());
				node.setReaderIo(getCellValue(row.getCell(12)).toString());
			}
			nodeService.save(node);
        }
	}
	
	
	@RequestMapping(value = "/forMap")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object forMap(String mapId) {
		ResultData resultData = new ResultData();
		List<Node> list = this.nodeService.findByMapIdAndHasEvent(mapId);
		List<NodeVo> returnList = new ArrayList<NodeVo>();
		for(Node node : list) {
			NodeVo  nodeVo = new NodeVo();
			BeanUtils.copyProperties(node, nodeVo);
			if(node.getStatus()!=null) {
				nodeVo.setStatusCode(node.getStatus().getName());
			}
			NodeType nodeType = nodeTypeService.findById(node.getNodeTypeId());
			NodeTypeVo nodeTypeVo = new NodeTypeVo();
			BeanUtils.copyProperties(nodeType, nodeTypeVo);
			nodeTypeVo.setWorkingIconUrl(fileService.getUrl(nodeType.getWorkingIcon()));
			nodeTypeVo.setStopIconUrl(fileService.getUrl(nodeType.getStopIcon()));
			nodeVo.setNodeType(nodeTypeVo);
			returnList.add(nodeVo);
		}
		resultData.setData("node", returnList);
		return resultData;
	}
	
	private Object getCellValue(Cell cell) {
		if(cell == null) {
			return "";
		}
		Object object = null;
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
	
	/**
	 * node2nodeVo
	 * @param node
	 * @return
	 */
	private NodeVo convertNodeVo(Node node) {
		if(node == null) {
			return null;
		}
		NodeVo  nodeVo = new NodeVo();
		BeanUtils.copyProperties(node, nodeVo);
		if(node.getStatus()!=null) {
			nodeVo.setStatusCode(node.getStatus().getName());
		}
		
		NodeType nodeType = nodeTypeService.findById(node.getNodeTypeId());
		NodeTypeVo nodeTypeVo = new NodeTypeVo();
		BeanUtils.copyProperties(nodeType, nodeTypeVo);
		nodeTypeVo.setWorkingIconUrl(fileService.getUrl(nodeType.getWorkingIcon()));
		nodeTypeVo.setStopIconUrl(fileService.getUrl(nodeType.getStopIcon()));
		nodeVo.setNodeType(nodeTypeVo);
		
		com.pepper.model.emap.map.Map  entity = mapService.findById(node.getMapId());
		MapVo  mapVo = new MapVo();
		BeanUtils.copyProperties(entity, mapVo);
		mapVo.setMapImageUrl(mapImageUrlService.findByMapId(entity.getId()));
		BuildingInfo buildingInfo =  buildingInfoService.findById(entity.getBuildId());
		BuildingInfoVo buildingInfoVo = new BuildingInfoVo();
		BeanUtils.copyProperties(buildingInfo, buildingInfoVo);
		SiteInfo siteInfo = siteInfoService.findById(buildingInfo.getSiteInfoId());
		buildingInfoVo.setSite(siteInfo);
		mapVo.setBuild(buildingInfoVo);
		nodeVo.setMap(mapVo);
		
		return nodeVo;
	}
	
	
}
