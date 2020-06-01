package com.pepper.controller.emap.common.timer;

import com.pepper.model.emap.scheduler.Scheduler;
import com.pepper.service.emap.event.EventListService;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.scheduler.SchedulerService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName CleanScheduler
 * @Description: 清除日志和事件
 * @Author mr.liu
 * @Date 2020/5/29
 **/
@Component
@Order(value= Ordered.LOWEST_PRECEDENCE)
public class CleanScheduler {

    @Reference
    private EventListService eventListService;

    @Reference
    private SystemLogService systemLogService;

    @Reference
    private SchedulerService schedulerService;

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void scheduled() {
        List<Scheduler> list =  schedulerService.findAll();
        int cleanEventDay = list.size()>0?list.get(0).getCleanEventDay():0;
        if(Objects.nonNull(cleanEventDay) && cleanEventDay>=0){
            Calendar calendar = new GregorianCalendar();
            calendar.add(calendar.DATE,cleanEventDay*-1);
            eventListService.delete(calendar.getTime());
        }

        int cleanLogDay = list.size()>0?list.get(0).getCleanLogDay():0;
        if(Objects.nonNull(cleanLogDay) && cleanLogDay>=0){
            Calendar calendar = new GregorianCalendar();
            calendar.add(calendar.DATE,cleanLogDay*-1);
            systemLogService.delete(calendar.getTime());
        }
    }
}
