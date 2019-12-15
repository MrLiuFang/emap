package com.pepper.controller.emap.common.timer;

import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.model.emap.department.DepartmentGroup;
import com.pepper.model.emap.scheduling.Scheduling;
import com.pepper.service.console.admin.user.AdminUserService;
import com.pepper.service.emap.department.DepartmentGroupService;
import com.pepper.service.emap.scheduling.SchedulingService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@Order(value= Ordered.LOWEST_PRECEDENCE)
public class SchedulingScheduler {

    @Reference
    private SchedulingService schedulingService;

    @Reference
    private AdminUserService adminUserService;

    @Reference
    private DepartmentGroupService departmentGroupService;

    @Scheduled(fixedRate = 1000*60)
    public void scheduled() {
        LocalDate nowDate = LocalDate.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = nowDate.atStartOfDay().atZone(zone).toInstant();
        java.util.Date date = Date.from(instant);
        List<Scheduling>  list = schedulingService.findByDate(date);
        for(Scheduling scheduling : list){
            if(StringUtils.hasText(scheduling.getUserId())){
                AdminUser adminuser = adminUserService.findById(scheduling.getUserId());
                if(Objects.nonNull(adminuser)){
                    adminuser.setDepartmentGroupId(scheduling.getDepartmentGroupId());
                    if(Objects.nonNull(scheduling.getManager()) && scheduling.getManager()){
                        adminuser.setIsManager(true);
                    }else {
                        adminuser.setIsManager(false);
                    }
                    if(!StringUtils.hasText(scheduling.getDepartmentId())){
                        DepartmentGroup departmentGroup = departmentGroupService.findById(scheduling.getDepartmentGroupId());
                        adminuser.setDepartmentId(departmentGroup.getDepartmentId());
                    }else {
                        adminuser.setDepartmentId(scheduling.getDepartmentId());
                    }

                    adminUserService.update(adminuser);
                }
            }
        }
    }
}
