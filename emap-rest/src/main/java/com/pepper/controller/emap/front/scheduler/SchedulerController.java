package com.pepper.controller.emap.front.scheduler;

import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.model.emap.scheduler.Scheduler;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.scheduler.SchedulerService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName SchedulerController
 * @Description: TODO
 * @Author mr.liu
 * @Date 2020/5/29
 **/
@Controller()
@RequestMapping(value = "/front/scheduler")
@Validated
public class SchedulerController extends BaseControllerImpl implements BaseController {

    @Reference
    private SchedulerService schedulerService;

    @RequestMapping(value = "/info")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object getScheuler(){
        ResultData resultData = new ResultData();
        List<Scheduler> list =  schedulerService.findAll();
        resultData.setData("cleanEventDay",list.size()>0?list.get(0).getCleanEventDay():0);
        resultData.setData("cleanLogDay",list.size()>0?list.get(0).getCleanLogDay():0);
        return resultData;
    }
    @RequestMapping(value = "/update")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object uddate(@RequestBody Scheduler scheduler){
        ResultData resultData = new ResultData();
        schedulerService.deleteAll();
        schedulerService.save(scheduler);
        return resultData;
    }

}
