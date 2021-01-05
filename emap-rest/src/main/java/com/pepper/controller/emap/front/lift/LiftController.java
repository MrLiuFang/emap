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
import com.pepper.core.exception.BusinessException;
import com.pepper.model.emap.lift.*;
import com.pepper.model.emap.staff.Staff;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

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
//                Floor floor = floorService.findByName(f.getName());
//                if (Objects.isNull(floor)){
//                    f = this.floorService.save(f);
//                }
                Floor floor1 = floorService.find(liftId,f.getName());
                if (Objects.nonNull(floor1)){
                    this.floorService.save(f);
                }
//                LiftFloor temp = liftFloorSevice.findLiftFloor(liftId,f.getId());
                if (Objects.isNull(floor1)) {
                    LiftFloor liftFloor = new LiftFloor();
                    liftFloor.setFloorId(f.getId());
                    liftFloor.setLiftId(liftId);
                    this.liftFloorSevice.save(liftFloor);
                }
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
            Floor floor = this.floorService.findById(s);
            if (Objects.isNull(floor)){
                new BusinessException("沒有該樓層");
            }
            Floor tmp = floorService.find(liftId,floor.getName());
            if (Objects.nonNull(tmp)){
//                floorService.delete(floor);
                new BusinessException("該電梯已有該樓層");
            }
            if (Objects.isNull(tmp)){
                LiftFloor liftFloor = new LiftFloor();
                liftFloor.setLiftId(liftId);
                liftFloor.setFloorId(s);
                LiftFloor temp = liftFloorSevice.findLiftFloor(liftId,s);
                if (Objects.nonNull(temp)){
                    new BusinessException("該電梯已有該樓層");
                }
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
                LiftFloor temp = liftFloorSevice.findLiftFloor(liftId,s);
                if (Objects.nonNull(temp)){
                    new BusinessException("該電梯已有該樓層");
                }
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
        Object obj = map.get("floorId");
        String floorId = Objects.nonNull(obj)?obj.toString():null;
        if (StringUtils.hasText(floorId)){
            liftFloorSevice.deleteByLiftId(liftId);
        }else {
            liftFloorSevice.deleteByLiftIdAndFloorId(liftId,floorId);
        }

        systemLogService.log("liftFloor delete", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/lift/right/addOrDelete")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object addOrDeleteLiftRight(@RequestBody Map<String,Object> map){
        List<String> floorId = (List<String>) map.get("floorId");
        List<String> staffId = (List<String>) map.get("staffId");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
                        Object startObj = map.get("startDate");
                        Object endObj = map.get("endDate");
                        String startDate = Objects.nonNull(startObj)?startObj.toString():"";
                        String endDate = Objects.nonNull(endObj)?startObj.toString():"";
                        if (StringUtils.hasText(startDate)) {
                            liftRight.setStartDate(simpleDateFormat.parse(startDate));
                        }
                        if (StringUtils.hasText(endDate)) {
                            liftRight.setEndDate(simpleDateFormat.parse(endDate));
                        }
                    } catch (ParseException e) {
//                        e.printStackTrace();
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
                        if (Objects.nonNull(liftRight.getEndDate())) {
                            liftRightVo.setEndDate(simpleDateFormat.format(liftRight.getEndDate()));
                        }
                        if (Objects.nonNull(liftRight.getStartDate())) {
                            liftRightVo.setStartDate(simpleDateFormat.format(liftRight.getStartDate()));
                        }
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
    public void exportLiftRight() throws IOException,
            IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		systemLogService.log("department export", this.request.getRequestURL().toString());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/xlsx");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("right.xlsx", "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        List<LiftRightExportVo> list = new ArrayList<LiftRightExportVo>();
        List<Lift> listLift = this.liftService.findAll();
        List<Staff> staffList = this.staffService.findAll();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        staffList.forEach(s->{
            listLift.forEach(lift -> {
                List<Floor> floorList = floorService.findByLiftId(lift.getId());
                if (floorList.size()>0){
                    floorList.forEach(floor -> {
                        LiftRightExportVo liftRightExportVo = new LiftRightExportVo();
                        FloorVo floorVo = new FloorVo();
                        BeanUtils.copyProperties(floor,floorVo);
                        LiftRight liftRight = liftRightService.find(s.getId(),lift.getId(),floor.getId());
                        if (Objects.nonNull(liftRight)){
                            if (Objects.nonNull(liftRight.getEndDate())) {
                                liftRightExportVo.setEndDate("'"+simpleDateFormat.format(liftRight.getEndDate()));
                            }
                            if (Objects.nonNull(liftRight.getStartDate())) {
                                liftRightExportVo.setStartDate("'"+simpleDateFormat.format(liftRight.getStartDate()));
                            }
                        }
                        floorVo.setRight(Objects.nonNull(liftRight));
                        liftRightExportVo.setFloorVo(floorVo);
                        liftRightExportVo.setLift(lift);
                        liftRightExportVo.setStaff(s);
                        if (StringUtils.hasText(s.getIdCard())){
                            list.add(liftRightExportVo);
                        }
                    });
                }
            });
        });

        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("idCard", "staff.idCard"));
        excelColumn.add(ExcelColumn.build("liftName", "lift.name"));
        excelColumn.add(ExcelColumn.build("floorName", "floorVo.name"));
        excelColumn.add(ExcelColumn.build("startDate", "startDate"));
        excelColumn.add(ExcelColumn.build("endDate", "endDate"));
        excelColumn.add(ExcelColumn.build("isRight", "floorVo.isRight"));
        new ExportExcelUtil().export((Collection<?>) list, outputStream, excelColumn);
    }

    @RequestMapping(value = "/lift/right/import")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object importRight(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException, ParseException {
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
                String idCard = getCellValue(row.getCell(0)).toString();
                String liftName = getCellValue(row.getCell(1)).toString();
                String floorName = getCellValue(row.getCell(2)).toString();
                String startDate = getCellValue(row.getCell(3)).toString();
                String endDate = getCellValue(row.getCell(4)).toString();
                String isRight = getCellValue(row.getCell(5)).toString();
                if ( Boolean.valueOf(isRight)){
                    List<Staff> staffList = this.staffService.findByIdCard(idCard);
                    Staff staff = staffList.size()>0?staffList.get(0):null;
                    if (Objects.nonNull(staff)) {
                        liftRight.setStaffId(staff.getId());
                    }else {
                        continue;
                    }
                    if (StringUtils.hasText(startDate) ) {
                        liftRight.setEndDate(simpleDateFormat.parse(endDate.replace("'","")));
                    }
                    if (StringUtils.hasText(endDate) ) {
                        liftRight.setStartDate(simpleDateFormat.parse(startDate.replace("'","")));
                    }
//                    Floor floor = this.floorService.findByName(floorName);
//                    if (Objects.nonNull(floor)){
//                        liftRight.setFloorId(floor.getId());
//                    }else {
//                        continue;
//                    }

                    Lift lift = this.liftService.findByName(liftName);
                    if (Objects.nonNull(lift)){
                        liftRight.setLiftId(lift.getId());
                    }else {
                        continue;
                    }

                    Floor floor = floorService.find(lift.getId(),floorName);
                    if (Objects.nonNull(floor)){
                        liftRight.setFloorId(floor.getId());
                    }else {
                        continue;
                    }

                    LiftRight tmp = liftRightService.find(staff.getId(),lift.getId(),floor.getId());
                    if (Objects.isNull(tmp)) {
                        this.liftRightService.save(liftRight);
                    }
                }
            }
        }

        systemLogService.log("import lift right");
        return resultData;
    }

    private Boolean check2(Row row) {
        if(!getCellValue(row.getCell(0)).toString().equals("idCard")) {
            return false;
        }
        if(!getCellValue(row.getCell(1)).toString().equals("liftName")) {
            return false;
        }
        if(!getCellValue(row.getCell(2)).toString().equals("floorName")) {
            return false;
        }
        if(!getCellValue(row.getCell(3)).toString().equals("startDate")) {
            return false;
        }
        if(!getCellValue(row.getCell(4)).toString().equals("endDate")) {
            return false;
        }
        if(!getCellValue(row.getCell(5)).toString().equals("isRight")) {
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
    public void exportLiftRightVip() throws IOException,
            IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		systemLogService.log("department export", this.request.getRequestURL().toString());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/xlsx");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("rightVip.xlsx", "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        List<Staff> staffList = this.staffService.findAll();
        List<Lift> listLift = this.liftService.findAll();
        List<LiftRightVipExport> list = new ArrayList<LiftRightVipExport>();
        staffList.forEach(s->{
            LiftRightVipExport liftRightVipExport = new LiftRightVipExport();
            listLift.forEach(lift -> {
                liftRightVipExport.setStaff(s);
                LiftRightVip liftRightVip = this.liftRightVipService.findFirstByStaffIdAndLiftId(s.getId(),lift.getId());
                if (Objects.nonNull(liftRightVip)){
                    liftRightVipExport.setLift(lift);
                    if (StringUtils.hasText(s.getIdCard())) {
                        list.add(liftRightVipExport);
                    }
                }
            });
        });
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("idCard", "staff.idCard"));
        excelColumn.add(ExcelColumn.build("liftName", "lift.name"));
        new ExportExcelUtil().export((Collection<?>) list, outputStream, excelColumn);
    }

    @RequestMapping(value = "/lift/right/vip/import")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object importRightVip(StandardMultipartHttpServletRequest multipartHttpServletRequest) throws IOException, ParseException {
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
                String idCard = getCellValue(row.getCell(0)).toString();
                String liftName = getCellValue(row.getCell(1)).toString();
                Lift lift = this.liftService.findByName(liftName);
                List<Staff> staffList = this.staffService.findByIdCard(idCard);
                Staff staff = staffList.size()>0?staffList.get(0):null;
                if (Objects.nonNull(lift) && Objects.nonNull(staff)){
                    LiftRightVip liftRightVip = this.liftRightVipService.findFirstByStaffIdAndLiftId(staff.getId(),lift.getId());
                    if (Objects.isNull(liftRightVip)){
                        obj.setLiftId(lift.getId());
                        obj.setStaffId(staff.getId());
                        liftRightVipService.save(obj);
                    }
                }
            }
        }
        systemLogService.log("import lift right");
        return resultData;
    }
    private Boolean check3(Row row) {
        if(!getCellValue(row.getCell(0)).toString().equals("idCard")) {
            return false;
        }
        if(!getCellValue(row.getCell(1)).toString().equals("liftName")) {
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
        pager.setData("liftLog",convterLiftLog(pager.getResults()));
        pager.setResults(null);
        return pager;
    }

    @Value("${mssql.jdbc-url}")
    private String mssqlJdbcUrl;

    @Value("${mssql.username}")
    private String mssqlUsername;

    @Value("${mssql.password}")
    private String mssqlPassword;

    @RequestMapping(value = "/lift/kaba/syncData")
//    @Authorize(authorizeResources = false)
    @ResponseBody
    @Scheduled(cron = "0 0 2 * * ?")
    public Object kabaSyncData() throws SQLException {
        Map<String,Integer> map = new HashMap<>();
        map.put("RF",12);
        map.put("7F",11);map.put("6F",10);map.put("5F",9);map.put("4F",8);map.put("3F",7);map.put("2F",6);map.put("1F",5);
        map.put("B1",3);map.put("B2",2);
        map.put("F7",11);map.put("F6",10);map.put("F5",9);map.put("F4",8);map.put("F3",7);map.put("F2",6);map.put("F1",5);
        map.put("1B",3);map.put("2B",2);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(mssqlJdbcUrl, mssqlUsername, mssqlPassword);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT p.PersId, p.FullName, ISNULL(p.Email, '') EMAIL, ISNULL(p.currentCardNr, '') Card FROM hPerson P");
            while (rs.next()) {
                String fullName = rs.getString("FullName");
                String email = rs.getString("EMAIL");
                String card = rs.getString("Card").trim();
                if (StringUtils.hasText(card)){
                    List<Staff> staffList = this.staffService.findByIdCard(card);
                    Staff staff = staffList.size()>0?staffList.get(0):null;
                    if (Objects.isNull(staff)){
                        staff = new Staff();
                        staff.setEmail(email);
                        staff.setIdCard(card);
                        staff.setName(fullName);
                        staff = staffService.save(staff);
                    }
                }
            }

            rs = stmt.executeQuery("SELECT V1.Medium,  V1.ProfileDetailsValidFrom ProfileFrom, V1.ProfileDetailsValidTo ProfileTo,\n" +
                    "  t2.DeviceType T2DevType, T2.Address T2Address, T4.Name T4Name, T3.Name T3Name\n" +
                    "FROM vaReportPersRoomzone        V1\n" +
                    "  LEFT JOIN pPassagewayComponent T2 ON V1.RZoneID = t2.RZoneFK\n" +
                    "  LEFT JOIN pPassageway          t3 ON t2.PassagewayFK=t3.PassagewayID\n" +
                    "  LEFT JOIN pPeriphery           t4 ON (t2.DeviceType='EF' and t4.DeviceType='BO' and t2.Address = t4.DeviceAddress)\n" +
                    "WHERE (NOT t2.DeviceType IS NULL)\n" +
                    "ORDER BY V1.Medium, T3.Name, T4.Name");
            while (rs.next()) {
                String medium = rs.getString("Medium");
                String profileFrom = rs.getString("ProfileFrom");
                String profileTo = rs.getString("ProfileTo");
                String t4Name = rs.getString("T4Name");
                String t3Name = rs.getString("T3Name");
                List<Staff> staffList = this.staffService.findByIdCard(medium);
                Staff staff = staffList.size()>0?staffList.get(0):null;
                if (Objects.nonNull(staff)){
//                    this.liftRightService.deleteByStaffId(staff.getId());
                    Lift lift = this.liftService.findByName(t3Name);
                    if (Objects.isNull(lift)){
                        lift = new Lift();
                        lift.setUartId(1);
                        lift.setName(t3Name);
                        lift = this.liftService.save(lift);
                    }
                    String[] floors = t4Name.replace(t3Name,"").trim().split("-");
                    String startFloor = floors[0];
                    String endFloor = floors[1];
                    if (!StringUtils.hasText(startFloor) || !StringUtils.hasText(endFloor)){
                        continue;
                    }
                    if (!map.containsKey(startFloor) || !map.containsKey(endFloor)){
                        continue;
                    }
                    for (int i = map.get(startFloor);i<=map.get(endFloor);i++){
                        int finalI = i;
                        Lift finalLift = lift;
                        map.forEach((k, v)->{
                            if (v== finalI){
                                Floor floor = floorService.find(finalLift.getId(),k);
                                if (Objects.isNull(floor)){
                                    floor = new Floor();
                                    floor.setName(k);
                                    floor.setFloor(v);
                                    floor = this.floorService.save(floor);
                                }
                                LiftFloor liftFloor = this.liftFloorSevice.findLiftFloor(finalLift.getId(),floor.getId());
                                if (Objects.isNull(liftFloor)){
                                    liftFloor = new LiftFloor();
                                    liftFloor.setLiftId(finalLift.getId());
                                    liftFloor.setFloorId(floor.getId());
                                    liftFloor = this.liftFloorSevice.save(liftFloor);
                                }
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
                                LiftRight liftRight = this.liftRightService.find(staff.getId(),finalLift.getId(),floor.getId());
                                if (Objects.isNull(liftRight)) {
                                    liftRight = new LiftRight();
                                    liftRight.setLiftId(finalLift.getId());
                                    liftRight.setFloorId(floor.getId());
                                    liftRight.setStaffId(staff.getId());
                                    if (StringUtils.hasText(profileFrom) && profileFrom.split(" ").length > 1) {
                                        try {
                                            liftRight.setStartDate(simpleDateFormat.parse(profileFrom.split(" ")[0]));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (StringUtils.hasText(profileTo) && profileTo.split(" ").length > 1) {
                                        try {
                                            liftRight.setEndDate(simpleDateFormat.parse(profileTo.split(" ")[0]));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    liftRight = this.liftRightService.save(liftRight);
                                }
                            }
                        });
                    }
                }
            }

            Map<String,String> map1 = new HashMap<String,String>();
            map1.put("I0100010203","ELE1,ELE2,ELE3");map1.put("I0100010503","ELE4");map1.put("I0100020703","ELE1,ELE2,ELE3");map1.put("I0100020403","ELE4");
            map1.put("I0100020403","ELE4");map1.put("I0100030503","ELE1,ELE2,ELE3");map1.put("I0100130103","ELE4");map1.put("I0100130104","ELE5");
            map1.put("I0100150203","ELE1,ELE2,ELE3");map1.put("I0100160403","ELE4");map1.put("I0100160404","ELE5");map1.put("I0100210303","ELE1,ELE2,ELE3");
            map1.put("I0100200103","ELE4");map1.put("I0100200104","ELE5");map1.put("I0100250703","ELE1,ELE2,ELE3");map1.put("I0100240703","ELE4");
            map1.put("I0100240704","ELE5");map1.put("I0100260803","ELE1,ELE2,ELE3");map1.put("I0100270703","ELE4");map1.put("I0100260303","ELE1,ELE2,ELE3");
            map1.put("I0100270404","ELE5");map1.put("I0100270403","ELE4");map1.put("I0100270704","ELE5");map1.put("I0100290403","ELE1,ELE2,ELE3");
            map1.put("I0100300503","ELE4");map1.put("I0100300504","ELE5");map1.put("I0100320403","ELE1,ELE2,ELE3");map1.put("I0100330303","ELE4");
            map1.put("I0100330304","ELE5");map1.put("I0100340203","ELE1,ELE2,ELE3");
            List<Staff> staffList = this.staffService.findAll();
            for (Staff s: staffList){
                String sql =" SELECT * FROM vpUserAccessRight WHERE BadgeNr='"+s.getIdCard()+"' and ReaderType='RU' " +
                        "AND (ReaderAddress ='I0100010203' or ReaderAddress ='I0100010503' or ReaderAddress ='I0100020703' or ReaderAddress ='I0100020403'" +
                        " or ReaderAddress ='I0100020403' or ReaderAddress ='I0100030503' or ReaderAddress ='I0100130103' or ReaderAddress ='I0100130104' " +
                        " or ReaderAddress ='I0100150203' or ReaderAddress ='I0100160403' or ReaderAddress ='I0100160404' or ReaderAddress ='I0100210303' " +
                        " or ReaderAddress ='I0100200103' or ReaderAddress ='I0100200104' or ReaderAddress ='I0100250703' or ReaderAddress ='I0100240703' " +
                        " or ReaderAddress ='I0100240704' or ReaderAddress ='I0100260803' or ReaderAddress ='I0100270703' or ReaderAddress ='I0100260303' " +
                        " or ReaderAddress ='I0100300503' or ReaderAddress ='I0100300504' or ReaderAddress ='I0100320403' or ReaderAddress ='I0100330303' " +
                        " or ReaderAddress ='I0100330304' or ReaderAddress ='I0100340203')";
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    String readerAddress = rs.getString("ReaderAddress");
                    if (map1.containsKey(readerAddress) && Objects.nonNull(map1.get(readerAddress))){
                        String[] str =map1.get(readerAddress).split(",");
                        for (String s1 : str)  {
                            Lift lift = liftService.findByName(s1);
                            if (Objects.isNull(lift)){
                                lift = new Lift();
                                lift.setName(s1);
                                lift.setUartId(1);
                                lift = this.liftService.save(lift);
                            }
                            if (Objects.nonNull(lift)){
                                LiftRightVip liftRightVip = liftRightVipService.findFirstByStaffIdAndLiftId(s.getId(),lift.getId());
                                if (Objects.isNull(liftRightVip)){
                                    liftRightVip = new LiftRightVip();
                                    liftRightVip.setStaffId(s.getId());
                                    liftRightVip.setLiftId(lift.getId());
                                    this.liftRightVipService.save(liftRightVip);
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            rs.close();
            stmt.close();
            conn.close();
        }
        return new ResultData();
    }

    private List<LiftLogVo> convterLiftLog(List<LiftLog> listLiftLog){
        List<LiftLogVo> list = new ArrayList<LiftLogVo>();
        listLiftLog.forEach(l->{
            LiftLogVo liftLogVo = new LiftLogVo();
            BeanUtils.copyProperties(l,liftLogVo);
            liftLogVo.setLift(liftService.findById(l.getLiftId()));
            list.add(liftLogVo);
        });
        return list;
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

class LiftLogVo extends LiftLog {
    private Lift lift;

    public Lift getLift() {
        return lift;
    }

    public void setLift(Lift lift) {
        this.lift = lift;
    }
}

class LiftRightVipExport {
    private Staff staff;

    private Lift lift;

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Lift getLift() {
        return lift;
    }

    public void setLift(Lift lift) {
        this.lift = lift;
    }
}

class LiftRightExportVo{
    private Lift lift;

    private Staff staff;

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

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}