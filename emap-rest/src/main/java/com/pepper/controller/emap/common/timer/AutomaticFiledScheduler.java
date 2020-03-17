package com.pepper.controller.emap.common.timer;

import com.pepper.model.emap.event.EventList;
import com.pepper.service.emap.event.EventListService;
import com.pepper.service.redis.string.serializer.ValueOperationsService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @description: 自动归档定时器
 * @author: Mr.Liu
 * @create: 2020-03-16 14:53
 */
@Component
@Order(value= Ordered.LOWEST_PRECEDENCE)
public class AutomaticFiledScheduler {

    @Resource
    private Environment environment;

    @Reference
    private EventListService eventListService;

    @Reference
    private ValueOperationsService valueOperationsService;

    @Scheduled(fixedRate = 5000)
    public void scheduled() {
        if(!environment.getProperty("scheduler.enabled", "true").equals("true")) {
            return;
        }
        Boolean automaticFiled = Boolean.valueOf(valueOperationsService.get("automaticFiled"));
        if(!automaticFiled){
            return ;
        }
        String sutomaticFiled = environment.getProperty("scheduler.automatic.filed", "60");
        List<EventList> list = eventListService.findByStatusNot("P");
        for(EventList eventList : list){
            Long time = new Date().getTime()-eventList.getCreateDate().getTime();
            if(time>= Long.valueOf(sutomaticFiled)*1000L){
                eventList.setStatus("P");
                eventListService.update(eventList);
            }
        }
    }
}
