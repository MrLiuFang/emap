package com.pepper.controller.emap.front.node;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Reference;
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
import com.pepper.common.emuns.Status;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
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
import com.pepper.service.emap.log.SystemLogService;
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
	
	@Reference
	private SystemLogService systemLogService;
	
	@RequestMapping(value = "/list")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object list(String code,String name,String source,String sourceCode,String mapId,String nodeTypeId,String siteId,String buildId,String floor,String hasXY) {
		Pager<Node> pager = new Pager<Node>();	
		pager = nodeService.findNavigator(pager,code,name,source,sourceCode,mapId,nodeTypeId,siteId,buildId,floor,hasXY);
		List<Node> list = pager.getResults();
		List<NodeVo> returnList = new ArrayList<NodeVo>();
		for(Node node : list) {
			returnList.add(convertNodeVo(node));
		}
		pager.setData("node",returnList);
		pager.setResults(null);
		systemLogService.log("get node list", this.request.getRequestURL().toString());
		return pager;
	}
	
	

	@RequestMapping(value = "/add")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object add(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Node node = new Node();
		MapToBeanUtil.convert(node, map);
		
		if(nodeService.findByCode(node.getCode())!=null) {
			resultData.setCode(2000001);
			resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
			return resultData;
		}
		
		if(nodeService.findBySourceCode(node.getSourceCode())!=null) {
			resultData.setCode(2000001);
			resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
			return resultData;
		}
		
		nodeService.save(node);
		systemLogService.log("node add", this.request.getRequestURL().toString());
		return resultData;
	}
	
	@RequestMapping(value = "/update")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object update(@RequestBody Map<String,Object> map) {
		ResultData resultData = new ResultData();
		Node node = new Node();
		MapToBeanUtil.convert(node, map);
		
		Node oldNode = nodeService.findByCode(node.getCode());
		if(oldNode!=null && oldNode.getCode()!=null&&node.getCode()!=null) {
			if(!node.getId().equals(oldNode.getId())){
				resultData.setCode(2000001);
				resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
				return resultData;
			}
		}
		
		Node oldNode1 = nodeService.findBySourceCode(node.getSourceCode());
		if(oldNode1!=null && oldNode1.getSourceCode()!=null&&node.getSourceCode()!=null) {
			if(!node.getId().equals(oldNode1.getId())){
				resultData.setCode(2000001);
				resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
				return resultData;
			}
		}
		
		nodeService.update(node);
		systemLogService.log("node update", this.request.getRequestURL().toString());
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
		systemLogService.log("node update status", this.request.getRequestURL().toString());
		return resultData;
	}
	
	
	@RequestMapping(value = "/toEdit")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object toEdit(String id) {
		ResultData resultData = new ResultData();
		Node node = nodeService.findById(id);
		resultData.setData("node",convertNodeVo(node));
		systemLogService.log("get node info", this.request.getRequestURL().toString());
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
		systemLogService.log("node delete", this.request.getRequestURL().toString());
		return resultData;
	}
	
//	@RequestMapping(value = "/importCamera")
//	@Authorize(authorizeResources = false)
//	@ResponseBody
//	public Object importCamera(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
//		ResultData resultData = new ResultData();
//		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
//		for (String fileName : files.keySet()) {
//			MultipartFile file = files.get(fileName);
//			importNode(file.getInputStream(),"camera");
//		}
//		systemLogService.log("node import camera", this.request.getRequestURL().toString());
//		return resultData;
//	}
//	
//	@RequestMapping(value = "/importDoor")
//	@Authorize(authorizeResources = false)
//	@ResponseBody
//	public Object importDoor(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
//		ResultData resultData = new ResultData();
//		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
//		for (String fileName : files.keySet()) {
//			MultipartFile file = files.get(fileName);
//			importNode(file.getInputStream(),"door");
//		}
//		systemLogService.log("node import door", this.request.getRequestURL().toString());
//		return resultData;
//	}
	
	@RequestMapping(value = "/import")
	@Authorize(authorizeResources = false)
	@ResponseBody
	public Object importExcel(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
		ResultData resultData = new ResultData();
		Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
		for (String fileName : files.keySet()) {
			MultipartFile file = files.get(fileName);
			return importNode(file.getInputStream());
		}
		systemLogService.log("node import camera", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private ResultData importNode(InputStream inputStream) throws IOException {
		ResultData resultData = new ResultData();
		if (inputStream == null) {
			return resultData;
		}
		Workbook wookbook = new XSSFWorkbook(inputStream);
        Sheet sheet = wookbook.getSheetAt(0);
        Row rowHead = sheet.getRow(0);
		int totalRowNum = sheet.getLastRowNum();
		List<Node> list = new ArrayList<Node>();
		if(!check(sheet.getRow(0))) {
			resultData.setCode(4000003);
			resultData.setMessage("数据错误！（非设备结构数据）");
			return resultData;
		}
		for(int i = 1 ; i <= totalRowNum ; i++)
        {
			Node node = new Node();
			node.setStatus(Status.NORMAL);
			Row row = sheet.getRow(i);
			node.setCode(getCellValue(row.getCell(0)).toString());
			node.setName(getCellValue(row.getCell(1)).toString());
			node.setSource(getCellValue(row.getCell(2)).toString());
			node.setSourceCode(getCellValue(row.getCell(3)).toString());
			
			List<com.pepper.model.emap.map.Map> listMap = this.mapService.findByName(getCellValue(row.getCell(4)).toString());
			
			if(listMap.size()!=1) {
				resultData.setCode(4000003);
				resultData.setMessage("数据错误！第"+i+"行，map数据错误");
				return resultData;
			}else {
				node.setMapId(listMap.get(0).getId());
			}
			
			node.setNodeTypeId(getCellValue(row.getCell(5)).toString());
			node.setX(getCellValue(row.getCell(6)).toString());
			node.setY(getCellValue(row.getCell(7)).toString());
			node.setWarningLevel(Integer.valueOf(getCellValue(row.getCell(10)).toString()));
			if(!StringUtils.hasText(node.getCode())) {
				resultData.setCode(4000003);
				resultData.setMessage("数据错误！第"+i+"行，code数据错误");
				return resultData;
			}
			
			if(!StringUtils.hasText(node.getName())) {
				resultData.setCode(4000003);
				resultData.setMessage("数据错误！第"+i+"行，name数据错误");
				return resultData;
			}
			
			if(!StringUtils.hasText(node.getSource())) {
				resultData.setCode(4000003);
				resultData.setMessage("数据错误！第"+i+"行，source数据错误");
				return resultData;
			}
			
			if(!StringUtils.hasText(node.getSourceCode())) {
				resultData.setCode(4000003);
				resultData.setMessage("数据错误！第"+i+"行,sourceCode数据错误");
				return resultData;
			}
			
			if(!StringUtils.hasText(node.getMapId())) {
				resultData.setCode(4000003);
				resultData.setMessage("数据错误！第"+i+"行,map数据错误");
				return resultData;
			}
			
			if(node.getNodeTypeId().equals("camera")) {
				node.setIp(getCellValue(row.getCell(8)).toString());
				node.setExternalLink(getCellValue(row.getCell(9)).toString());
				String hasPtz = getCellValue(row.getCell(11)).toString().toLowerCase();
				if(StringUtils.hasText(hasPtz)&&(hasPtz.equals("true")||hasPtz.equals("false"))) {
					node.setHasPtz(Boolean.valueOf(hasPtz));
				}else {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，hasPtz数据错误");
					return resultData;
				}
				node.setUserName(getCellValue(row.getCell(12)).toString());
				node.setPassword(getCellValue(row.getCell(13)).toString());
				node.setSystemID(getCellValue(row.getCell(14)).toString());
				node.setWindowsUser(getCellValue(row.getCell(15)).toString());
				node.setWindowsPass(getCellValue(row.getCell(16)).toString());
				node.setDomainName(getCellValue(row.getCell(17)).toString());
				
				if(!StringUtils.hasText(node.getIp())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行,map数据错误");
					return resultData;
				}
				
				if(!StringUtils.hasText(node.getExternalLink())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行,externalLink数据错误");
					return resultData;
				}
				
				if(!StringUtils.hasText(node.getUserName())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行userName数据错误");
					return resultData;
				}
				
				if(!StringUtils.hasText(node.getPassword())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行password数据错误");
					return resultData;
				}
				
				if(!StringUtils.hasText(node.getSystemID())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行systemID数据错误");
					return resultData;
				}
				if(!StringUtils.hasText(node.getWindowsPass())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行windowsPass数据错误");
					return resultData;
				}
				if(!StringUtils.hasText(node.getWindowsPass())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行windowsPass数据错误");
					return resultData;
				}
				if(!StringUtils.hasText(node.getDomainName())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行domainName数据错误");
					return resultData;
				}
			}else if(node.getNodeTypeId().equals("door")) {
				node.setPaneId(getCellValue(row.getCell(18)).toString());
				node.setPaneIp(getCellValue(row.getCell(19)).toString());
				node.setReaderId(getCellValue(row.getCell(20)).toString());
				node.setReaderIo(getCellValue(row.getCell(21)).toString());
				
				if(!StringUtils.hasText(node.getPaneId())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行paneId数据错误");
					return resultData;
				}
				if(!StringUtils.hasText(node.getPaneIp())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行paneIp数据错误");
					return resultData;
				}
				if(!StringUtils.hasText(node.getReaderId())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行readerId数据错误");
					return resultData;
				}
				if(!StringUtils.hasText(node.getReaderIo())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行readerIo数据错误");
					return resultData;
				}
			}else {
				List<NodeType> listNodeType = this.nodeTypeService.findByName(node.getNodeTypeId());
				if(listNodeType.size()!=1) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行，nodeType数据错误");
					return resultData;
				}else {
					node.setNodeTypeId(listNodeType.get(0).getId());
				}
				
				if(!StringUtils.hasText(node.getNodeTypeId())) {
					resultData.setCode(4000003);
					resultData.setMessage("数据错误！第"+i+"行nodeType数据错误");
					return resultData;
				}
			}
			
			if(nodeService.findByCode(node.getCode())!=null) {
				resultData.setCode(4000003);
				resultData.setMessage("数据错误！第"+i+"行，"+node.getCode()+"已存在");
				return resultData;
			}
			
			if(nodeService.findBySourceCode(node.getSourceCode())!=null) {
				resultData.setCode(4000003);
				resultData.setMessage("数据错误！第"+i+"行，"+node.getSourceCode()+"已存在");
				return resultData;
			}
			list.add(node);
			
        }
		this.nodeService.saveAll(list);
		systemLogService.log("node import", this.request.getRequestURL().toString());
		return resultData;
	}
	
	private Boolean check(Row row) {
		if(!getCellValue(row.getCell(0)).toString().equals("code")) {
			return false;
		}
		if(!getCellValue(row.getCell(1)).toString().equals("name")) {
			return false;
		}
		if(!getCellValue(row.getCell(2)).toString().equals("source")) {
			return false;
		}
		if(!getCellValue(row.getCell(3)).toString().equals("sourceCode")) {
			return false;
		}
		if(!getCellValue(row.getCell(4)).toString().equals("map")) {
			return false;
		}
		if(!getCellValue(row.getCell(5)).toString().equals("nodeType")) {
			return false;
		}
		if(!getCellValue(row.getCell(6)).toString().equals("x")) {
			return false;
		}
		if(!getCellValue(row.getCell(7)).toString().equals("y")) {
			return false;
		}
		if(!getCellValue(row.getCell(8)).toString().equals("ip")) {
			return false;
		}
		if(!getCellValue(row.getCell(9)).toString().equals("externalLink")) {
			return false;
		}
		if(!getCellValue(row.getCell(10)).toString().equals("warningLevel")) {
			return false;
		}
		if(!getCellValue(row.getCell(11)).toString().equals("hasPtz")) {
			return false;
		}
		if(!getCellValue(row.getCell(12)).toString().equals("userName")) {
			return false;
		}
		if(!getCellValue(row.getCell(13)).toString().equals("password")) {
			return false;
		}
		if(!getCellValue(row.getCell(14)).toString().equals("systemId")) {
			return false;
		}
		if(!getCellValue(row.getCell(15)).toString().equals("windowsUser")) {
			return false;
		}
		if(!getCellValue(row.getCell(16)).toString().equals("windowsPass")) {
			return false;
		}
		if(!getCellValue(row.getCell(17)).toString().equals("domainName")) {
			return false;
		}
		if(!getCellValue(row.getCell(18)).toString().equals("paneId")) {
			return false;
		}
		if(!getCellValue(row.getCell(19)).toString().equals("paneIp")) {
			return false;
		}
		if(!getCellValue(row.getCell(20)).toString().equals("readerId")) {
			return false;
		}
		if(!getCellValue(row.getCell(21)).toString().equals("readerIo")) {
			return false;
		}
		return true;
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
		systemLogService.log("get node for map list", this.request.getRequestURL().toString());
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
		if(entity==null) {
			return nodeVo;
		}
		MapVo  mapVo = new MapVo();
		BeanUtils.copyProperties(entity, mapVo);
		mapVo.setMapImageUrl(mapImageUrlService.findByMapId(entity.getId()));
		BuildingInfo buildingInfo =  buildingInfoService.findById(entity.getBuildId());
		if(buildingInfo==null) {
			return nodeVo;
		}
		BuildingInfoVo buildingInfoVo = new BuildingInfoVo();
		BeanUtils.copyProperties(buildingInfo, buildingInfoVo);
		SiteInfo siteInfo = siteInfoService.findById(buildingInfo.getSiteInfoId());
		if(siteInfo==null) {
			return nodeVo;
		}
		buildingInfoVo.setSite(siteInfo);
		mapVo.setBuild(buildingInfoVo);
		nodeVo.setMap(mapVo);
		
		return nodeVo;
	}
	
	
}
