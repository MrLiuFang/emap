package com.pepper.controller.emap.front.lift;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.controller.emap.util.ExcelColumn;
import com.pepper.controller.emap.util.ExportExcelUtil;
import com.pepper.core.Pager;
import com.pepper.core.ResultData;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.lift.*;
import com.pepper.model.emap.vo.FloorVo;
import com.pepper.model.emap.vo.LiftFloorVo;
import com.pepper.model.emap.vo.LiftRightVipVo;
import com.pepper.model.emap.vo.LiftRightVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.lift.*;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.staff.StaffService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller()
@RequestMapping(value = "/front")
public class LiftController extends BaseControllerImpl implements BaseController {

    @Reference
    private FloorService floorService;

    @Reference
    private LiftFloorSevice liftFloorSevice;

    @Reference
    private LiftRightService liftRightService;

    @Reference
    private LiftService liftService;

    @Reference
    private SystemLogService systemLogService;

    @Reference
    private LiftRightVipService liftRightVipService;

    @Reference
    private StaffService staffService;

    @Reference
    private LiftLogService liftLogService;

    @RequestMapping("/lift/add")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object add(@RequestBody Lift lift){
        ResultData resultData = new ResultData();
        this.liftService.save(lift);
        systemLogService.log("lift add", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/lift/list")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object list( String name, String ip) {
        Pager<Lift> pager = new Pager<Lift>();

        if(StringUtils.hasText(ip)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_ip",ip );
        }
        if(StringUtils.hasText(name)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
        }
        pager.getJpqlParameter().setSortParameter("createDate", Sort.Direction.DESC);
        pager = liftService.findNavigator(pager);
        pager.setData("lift",pager.getResults());
        pager.setResults(null);
        return pager;
    }

    @RequestMapping(value = "/lift/export")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public void export( String name, String ip) throws IOException,
            IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		systemLogService.log("department export", this.request.getRequestURL().toString());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/xlsx");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("lift.xlsx", "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        Pager<Lift> pager = new Pager<Lift>();
        pager.setPageSize(Integer.MAX_VALUE);
        if(StringUtils.hasText(ip)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_ip",ip );
        }
        if(StringUtils.hasText(name)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
        }
        pager.getJpqlParameter().setSortParameter("createDate", Sort.Direction.DESC);
        pager = liftService.findNavigator(pager);
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("name", "name"));
        excelColumn.add(ExcelColumn.build("ip", "ip"));
        excelColumn.add(ExcelColumn.build("uartId", "uartId"));
        new ExportExcelUtil().export((Collection<?>) pager.getResults(), outputStream, excelColumn);
    }

    @RequestMapping(value = "/lift/import")
	@Authorize(authorizeResources = false)
    @ResponseBody
    public Object importStaff(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
        Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
        List<Lift> list = new ArrayList<Lift>();
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
                Lift lift = new Lift();
                lift.setName(getCellValue(row.getCell(0)).toString());
                lift.setIp(getCellValue(row.getCell(1)).toString());
                lift.setUartId(Integer.valueOf(row.getCell(2).toString().replace(".0","")));
                list.add(lift);

            }
            this.liftService.saveAll(list);
        }
        systemLogService.log("import lift");
        return resultData;
    }

    private  boolean isExcel2003(String filePath){
        return StringUtils.hasText(filePath) && filePath.endsWith(".xls");
    }
    private  boolean isExcel2007(String filePath){
        return StringUtils.hasText(filePath) && filePath.endsWith(".xlsx");
    }

    private Boolean check(Row row) {
        if(!getCellValue(row.getCell(0)).toString().equals("name")) {
            return false;
        }
        if(!getCellValue(row.getCell(1)).toString().equals("ip")) {
            return false;
        }
        if(!getCellValue(row.getCell(2)).toString().equals("uartId")) {
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

    @RequestMapping(value = "/lift/update")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object update(@RequestBody Lift lift) throws IOException {
        ResultData resultData = new ResultData();
        liftService.update(lift);
        systemLogService.log("lift update", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/lift/toEdit")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object toEdit(String id) {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
        resultData.setData("lift",this.liftService.findById(id));
        return resultData;
    }

    @RequestMapping(value = "/lift/delete")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object delete(@RequestBody String str) throws IOException {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
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
                this.liftRightService.delete(id);
                this.liftFloorSevice.deleteByLiftId(id);
                liftService.deleteById(id);
            }catch (Exception e) {
                // TODO: handle exception
            }
        }
        systemLogService.log("lift delete", this.request.getRequestURL().toString());
        return resultData;
    }


    @RequestMapping("/floor/add")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object addFloor(@RequestBody Floor floor){
        ResultData resultData = new ResultData();
        floor = this.floorService.save(floor);
        systemLogService.log("floor add", this.request.getRequestURL().toString());
        resultData.setData("id",floor.getId());
        return resultData;
    }

    @RequestMapping(value = "/floor/list")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object listFloor( String name, Integer floor) {
        Pager<Floor> pager = new Pager<Floor>();

        if(Objects.nonNull(floor)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.EQUAL+"_floor",floor );
        }
        if(StringUtils.hasText(name)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
        }
        pager.getJpqlParameter().setSortParameter("createDate", Sort.Direction.DESC);
        pager = floorService.findNavigator(pager);
        pager.setData("floor",pager.getResults());
        pager.setResults(null);
        return pager;
    }

    @RequestMapping(value = "/lift/floor/export")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public void exportFloor( String liftId, String floorId) throws IOException,
            IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		systemLogService.log("department export", this.request.getRequestURL().toString());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/xlsx");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("floor.xlsx", "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        Pager<LiftFloor> pager = new Pager<LiftFloor>();
        pager.setPageSize(Integer.MAX_VALUE);
        if(StringUtils.hasText(liftId)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_liftId",liftId );
        }
        if(StringUtils.hasText(floorId)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_floorId",floorId );
        }
        pager.getJpqlParameter().setSortParameter("createDate", Sort.Direction.DESC);
        pager = liftFloorSevice.findNavigator(pager);
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("name", "floor.name"));
        excelColumn.add(ExcelColumn.build("floor", "floor.floor"));
        new ExportExcelUtil().export((Collection<?>) convterLiftFloor(pager.getResults()), outputStream, excelColumn);
    }

    @RequestMapping(value = "/lift/floor/import")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object importFloor(StandardMultipartHttpServletRequest multipartHttpServletRequest,String liftId) throws IOException {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
        Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
        List<Floor> list = new ArrayList<Floor>();
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
            if(!check1(sheet.getRow(0))) {
                resultData.setMessage("数据错误！");
                return resultData;
            }
            for(int i = 1 ; i <= totalRowNum ; i++)
            {
                Row row = sheet.getRow(i);
                Floor floor = new Floor();
                floor.setName(getCellValue(row.getCell(0)).toString());
                floor.setFloor(Integer.valueOf(row.getCell(1).toString().replace(".0","")));
                list.add(floor);
            }
            list.forEach(f->{
                f = this.floorService.save(f);
                LiftFloor liftFloor = new LiftFloor();
                liftFloor.setFloorId(f.getId());
                liftFloor.setLiftId(liftId);
                this.liftFloorSevice.save(liftFloor);
            });
        }
        systemLogService.log("import lift");
        return resultData;
    }

    private Boolean check1(Row row) {
        if(!getCellValue(row.getCell(0)).toString().equals("name")) {
            return false;
        }
        if(!getCellValue(row.getCell(1)).toString().equals("floor")) {
            return false;
        }
        return true;
    }

    @RequestMapping(value = "/floor/update")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object updateFloor(@RequestBody Floor floor) throws IOException {
        ResultData resultData = new ResultData();
        floorService.update(floor);
        systemLogService.log("floor update", this.request.getRequestURL().toString());
        resultData.setData("id",floor.getId());
        return resultData;
    }

    @RequestMapping(value = "/floor/toEdit")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object toEditFloor(String id) {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
        resultData.setData("floor",this.floorService.findById(id));
        return resultData;
    }

    @RequestMapping(value = "/floor/delete")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object deleteFloor(@RequestBody String str) throws IOException {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
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
                this.liftFloorSevice.deleteByFloorId(id);
                this.liftRightService.deleteByFloorId(id);
                floorService.deleteById(id);
            }catch (Exception e) {
                // TODO: handle exception
            }
        }
        systemLogService.log("floor delete", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping("/lift/floor/add")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object addLiftFloor(@RequestBody Map<String,Object> map){
        ResultData resultData = new ResultData();
        List<String> list = (List<String>) map.get("floorId");
        String liftId = map.get("liftId").toString();
        list.forEach(s -> {
            LiftFloor tmp = liftFloorSevice.findLiftFloor(liftId,s);
            if (Objects.isNull(tmp)){
                LiftFloor liftFloor = new LiftFloor();
                liftFloor.setLiftId(liftId);
                liftFloor.setFloorId(s);
                liftFloorSevice.save(liftFloor);
            }
        });
        systemLogService.log("liftFloor add", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/lift/floor/list")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object listLiftFloor( String liftId, String floorId) {
        Pager<LiftFloor> pager = new Pager<LiftFloor>();

        if(StringUtils.hasText(liftId)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_liftId",liftId );
        }
        if(StringUtils.hasText(floorId)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_floorId",floorId );
        }
        pager.getJpqlParameter().setSortParameter("createDate", Sort.Direction.DESC);
        pager = liftFloorSevice.findNavigator(pager);
        pager.setData("liftFloor",convterLiftFloor(pager.getResults()));
        pager.setResults(null);
        return pager;
    }

    @RequestMapping(value = "/lift/floor/update")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object updateLiftFloor(@RequestBody Map<String,Object> map) throws IOException {
        ResultData resultData = new ResultData();
        List<String> list = (List<String>) map.get("floorId");
        String liftId = map.get("liftId").toString();
        liftFloorSevice.deleteByLiftId(liftId);
        list.forEach(s -> {
            LiftFloor tmp = liftFloorSevice.findLiftFloor(liftId,s);
            if (Objects.isNull(tmp)){
                LiftFloor liftFloor = new LiftFloor();
                liftFloor.setLiftId(liftId);
                liftFloor.setFloorId(s);
                liftFloorSevice.save(liftFloor);
            }
        });
        systemLogService.log("liftFloor update", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/lift/floor/toEdit")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object toEditLiftFloor(String liftId) {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
        Pager<LiftFloor> pager = new Pager<LiftFloor>();
        pager.setPageSize(Integer.MAX_VALUE);
        if(StringUtils.hasText(liftId)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_liftId",liftId );
        }
        pager.getJpqlParameter().setSortParameter("createDate", Sort.Direction.DESC);
        pager = liftFloorSevice.findNavigator(pager);
        resultData.setData("liftFloor",convterLiftFloor(pager.getResults()));
        return resultData;
    }

    @RequestMapping(value = "/lift/floor/delete")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object deleteLiftFloor(@RequestBody Map<String,Object> map) throws IOException {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
        String liftId = map.get("liftId").toString();
        liftFloorSevice.deleteByLiftId(liftId);
        systemLogService.log("liftFloor delete", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/lift/right/addOrDelete")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object addOrDeleteLiftRight(@RequestBody Map<String,Object> map){
        List<String> floorId = (List<String>) map.get("floorId");
        List<String> staffId = (List<String>) map.get("staffId");
        String startDate = map.get("startDate").toString();
        String endDate = map.get("endDate").toString();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
        staffId.forEach(s -> {
            liftRightService.deleteByStaffId(s);
            floorId.forEach(f->{
                LiftRight liftRight = new LiftRight();
                Lift lift =  liftService.findByFloorId(f);
                if (Objects.nonNull(lift)) {
                    liftRight.setStaffId(s);
                    liftRight.setLiftId(lift.getId());
                    liftRight.setFloorId(f);
                    try {
                        liftRight.setStartDate(simpleDateFormat.parse(startDate));
                        liftRight.setEndDate(simpleDateFormat.parse(endDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    liftRightService.save(liftRight);
                }
            });
        });

        ResultData resultData = new ResultData();
        return resultData;
    }

    @RequestMapping(value = "/lift/right/info")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object liftRightInfo(String staffId){
        List<LiftRightVo> listLiftRightVo = new ArrayList<LiftRightVo>();
        List<Lift> listLift = this.liftService.findAll();
        listLift.forEach(lift -> {
            LiftRightVo liftRightVo = new LiftRightVo();
            liftRightVo.setLift(lift);
            List<Floor> floorList = floorService.findByLiftId(lift.getId());
            if (floorList.size()>0){
                List<FloorVo> floorVoList = new ArrayList<FloorVo>();
                floorList.forEach(floor -> {
                    FloorVo floorVo = new FloorVo();
                    BeanUtils.copyProperties(floor,floorVo);
                    LiftRight liftRight = liftRightService.find(staffId,lift.getId(),floor.getId());
                    if (Objects.nonNull(liftRight)) {
                        BeanUtils.copyProperties(liftRight, liftRightVo);
                    }
                    floorVo.setRight(Objects.nonNull(liftRight));
                    floorVoList.add(floorVo);
                });
                liftRightVo.setFloors(floorVoList);
            }
            listLiftRightVo.add(liftRightVo);
        });
        ResultData resultData = new ResultData();
        resultData.setData("info",listLiftRightVo);
        return resultData;
    }

    @RequestMapping(value = "/lift/right/export")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public void exportLiftRight(String staffId) throws IOException,
            IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		systemLogService.log("department export", this.request.getRequestURL().toString());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/xlsx");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("right.xlsx", "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        List<LiftRightExportVo> list = new ArrayList<LiftRightExportVo>();
        List<Lift> listLift = this.liftService.findAll();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        listLift.forEach(lift -> {
            LiftRightExportVo liftRightExportVo = new LiftRightExportVo();
            List<Floor> floorList = floorService.findByLiftId(lift.getId());
            if (floorList.size()>0){
                floorList.forEach(floor -> {
                    FloorVo floorVo = new FloorVo();
                    BeanUtils.copyProperties(floor,floorVo);
                    LiftRight liftRight = liftRightService.find(staffId,lift.getId(),floor.getId());
                    if (Objects.nonNull(liftRight)){
                        if (Objects.nonNull(liftRight.getEndDate())) {
                            liftRightExportVo.setEndDate(simpleDateFormat.format(liftRight.getEndDate()));
                        }
                        if (Objects.nonNull(liftRight.getStartDate())) {
                            liftRightExportVo.setStartDate(simpleDateFormat.format(liftRight.getStartDate()));
                        }
                    }
                    floorVo.setRight(Objects.nonNull(liftRight));
                    liftRightExportVo.setFloorVo(floorVo);
                    liftRightExportVo.setLift(lift);
                    list.add(liftRightExportVo);
                });
            }
        });
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("liftId", "lift.id"));
        excelColumn.add(ExcelColumn.build("liftName", "lift.name"));
        excelColumn.add(ExcelColumn.build("floorId", "floorVo.id"));
        excelColumn.add(ExcelColumn.build("floorName", "floorVo.name"));
        excelColumn.add(ExcelColumn.build("startDate", "startDate"));
        excelColumn.add(ExcelColumn.build("endDate", "endDate"));
        excelColumn.add(ExcelColumn.build("isRight", "floorVo.isRight"));
        new ExportExcelUtil().export((Collection<?>) list, outputStream, excelColumn);
    }

    @RequestMapping(value = "/lift/right/import")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object importRight(StandardMultipartHttpServletRequest multipartHttpServletRequest,String staffId) throws IOException, ParseException {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
        Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
        List<LiftRight> list = new ArrayList<LiftRight>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
            if(!check2(sheet.getRow(0))) {
                resultData.setMessage("数据错误！");
                return resultData;
            }
            for(int i = 1 ; i <= totalRowNum ; i++)
            {
                Row row = sheet.getRow(i);
                LiftRight liftRight = new LiftRight();
                String liftId = getCellValue(row.getCell(0)).toString();
                String floorId = getCellValue(row.getCell(2)).toString();
                String startDate = getCellValue(row.getCell(4)).toString();
                String endDate = getCellValue(row.getCell(5)).toString();
                String isRight = getCellValue(row.getCell(6)).toString();
                if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate) && Boolean.valueOf(isRight)){
                    liftRight.setStaffId(staffId);
                    liftRight.setEndDate(simpleDateFormat.parse(endDate));
                    liftRight.setStartDate(simpleDateFormat.parse(startDate));
                    liftRight.setFloorId(floorId);
                    liftRight.setLiftId(liftId);
                    LiftRight tmp = liftRightService.find(staffId,liftId,floorId);
                    if (Objects.isNull(tmp)) {
                        list.add(liftRight);
                    }
                }
            }
        }
        this.liftRightService.saveAll(list);
        systemLogService.log("import lift right");
        return resultData;
    }

    private Boolean check2(Row row) {
        if(!getCellValue(row.getCell(0)).toString().equals("liftId")) {
            return false;
        }
        if(!getCellValue(row.getCell(1)).toString().equals("liftName")) {
            return false;
        }
        if(!getCellValue(row.getCell(2)).toString().equals("floorId")) {
            return false;
        }
        if(!getCellValue(row.getCell(3)).toString().equals("floorName")) {
            return false;
        }
        if(!getCellValue(row.getCell(4)).toString().equals("startDate")) {
            return false;
        }
        if(!getCellValue(row.getCell(5)).toString().equals("endDate")) {
            return false;
        }
        if(!getCellValue(row.getCell(6)).toString().equals("isRight")) {
            return false;
        }
        return true;
    }

    @RequestMapping(value = "/lift/right/vip/add")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object addLiftRightVip(@RequestBody LiftRightVip liftRightVip){
        liftRightVipService.save(liftRightVip);
        ResultData resultData = new ResultData();
        return resultData;
    }

    @RequestMapping(value = "/lift/right/vip/list")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object addLiftRightVip(String staffId){
        Pager<LiftRightVip> pager = new Pager<LiftRightVip>();
        pager.getJpqlParameter().setSortParameter("createDate", Sort.Direction.DESC);
        pager = liftRightVipService.List(pager,staffId);
        pager.setData("liftRightVip",convterliftRightVip(pager.getResults()));
        pager.setResults(null);
        return pager;
    }

    @RequestMapping(value = "/lift/right/vip/export")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public void exportLiftRightVip(String staffId) throws IOException,
            IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		systemLogService.log("department export", this.request.getRequestURL().toString());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/xlsx");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("rightVip.xlsx", "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        Pager<LiftRightVip> pager = new Pager<LiftRightVip>();
        pager.getJpqlParameter().setSortParameter("createDate", Sort.Direction.DESC);
        pager = liftRightVipService.List(pager,staffId);
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("liftName", "lift.name"));
        new ExportExcelUtil().export((Collection<?>) convterliftRightVip(pager.getResults()), outputStream, excelColumn);
    }

    @RequestMapping(value = "/lift/right/vip/import")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object importRightVip(StandardMultipartHttpServletRequest multipartHttpServletRequest,String staffId) throws IOException, ParseException {
        com.pepper.controller.emap.core.ResultData resultData = new com.pepper.controller.emap.core.ResultData();
        Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
        List<LiftRightVip> list = new ArrayList<LiftRightVip>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
            if(!check3(sheet.getRow(0))) {
                resultData.setMessage("数据错误！");
                return resultData;
            }
            for(int i = 1 ; i <= totalRowNum ; i++)
            {
                Row row = sheet.getRow(i);
                LiftRightVip obj = new LiftRightVip();
                String liftName = getCellValue(row.getCell(0)).toString();
                Lift lift = this.liftService.findByName(liftName);
                if (Objects.nonNull(lift)){
                    LiftRightVip liftRightVip = this.liftRightVipService.findFirstByStaffIdAndLiftId(staffId,lift.getId());
                    if (Objects.isNull(liftRightVip)){
                        obj.setLiftId(lift.getId());
                        obj.setStaffId(staffId);
                        list.add(obj);
                    }
                }
            }
        }
        this.liftRightVipService.saveAll(list);
        systemLogService.log("import lift right");
        return resultData;
    }
    private Boolean check3(Row row) {
        if(!getCellValue(row.getCell(0)).toString().equals("liftName")) {
            return false;
        }
        return true;
    }

    @RequestMapping(value = "/lift/log/list")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object liftLogList(String liftId, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate){
        Pager<LiftLog> pager = new Pager<LiftLog>();
        pager.getJpqlParameter().setSortParameter("createDate", Sort.Direction.DESC);
        pager =liftLogService.List(pager,liftId,startDate,endDate);
        pager.setData("liftLog",pager.getResults());
        pager.setResults(null);
        return pager;
    }


    private List<LiftRightVipVo> convterliftRightVip(List<LiftRightVip> liftRightVip){
        List<LiftRightVipVo> list = new ArrayList<LiftRightVipVo>();
        liftRightVip.forEach(l->{
            LiftRightVipVo liftRightVipVo = new LiftRightVipVo();
            BeanUtils.copyProperties(l,liftRightVipVo);
            liftRightVipVo.setStaff(staffService.findById(l.getStaffId()));
            liftRightVipVo.setLift(liftService.findById(l.getLiftId()));
            list.add(liftRightVipVo);
        });
        return list;
    }


    private List<LiftFloorVo> convterLiftFloor(List<LiftFloor> liftFloors){
        List<LiftFloorVo> list = new ArrayList<LiftFloorVo>();
        liftFloors.forEach(l->{
            LiftFloorVo liftFloorVo = new LiftFloorVo();
            BeanUtils.copyProperties(l,liftFloorVo);
            liftFloorVo.setFloor(this.floorService.findById(l.getFloorId()));
            liftFloorVo.setLift(this.liftService.findById(l.getLiftId()));
            list.add(liftFloorVo);
        });
        return list;
    }
}

class LiftRightExportVo{
    private Lift lift;

    private FloorVo floorVo;

    private String startDate;

    private String endDate;

    public Lift getLift() {
        return lift;
    }

    public void setLift(Lift lift) {
        this.lift = lift;
    }

    public FloorVo getFloorVo() {
        return floorVo;
    }

    public void setFloorVo(FloorVo floorVo) {
        this.floorVo = floorVo;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}