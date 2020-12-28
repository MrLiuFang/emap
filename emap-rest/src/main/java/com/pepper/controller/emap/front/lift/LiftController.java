package com.pepper.controller.emap.front.lift;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.core.Pager;
import com.pepper.core.ResultData;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.group.Group;
import com.pepper.model.emap.group.GroupBuild;
import com.pepper.model.emap.lift.Floor;
import com.pepper.model.emap.lift.Lift;
import com.pepper.model.emap.lift.LiftFloor;
import com.pepper.model.emap.lift.LiftRight;
import com.pepper.model.emap.vo.FloorVo;
import com.pepper.model.emap.vo.LiftFloorVo;
import com.pepper.model.emap.vo.LiftRightVo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.lift.FloorService;
import com.pepper.service.emap.lift.LiftFloorSevice;
import com.pepper.service.emap.lift.LiftRightService;
import com.pepper.service.emap.lift.LiftService;
import com.pepper.service.emap.log.SystemLogService;
import org.apache.dubbo.config.annotation.Reference;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
