package com.pepper.service.emap.scheduler;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.model.emap.report.ReportParameter;
import com.pepper.model.emap.scheduler.Scheduler;
import com.pepper.service.emap.report.ReportParameterService;
import com.pepper.service.emap.report.ReportService;
import org.apache.dubbo.config.annotation.Service;

/**
 * @ClassName SchedulerServiceImpl
 * @Description: TODO
 * @Author mr.liu
 * @Date 2020/5/29
 **/
@Service(interfaceClass = SchedulerService.class)
public class SchedulerServiceImpl extends BaseServiceImpl<Scheduler> implements SchedulerService {
}
