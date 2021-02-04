package com.pepper.service.emap.event.impl;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.event.EventListGroupDao;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.EventListGroup;
import com.pepper.service.emap.event.EventListGroupService;
import com.pepper.service.emap.event.EventListService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @description:
 * @author: 雄大
 * @create: 2021-02-04 17:18
 */
@Service(interfaceClass = EventListGroupService.class)
public class EventListGroupServiceImpl extends BaseServiceImpl<EventListGroup> implements EventListGroupService {

    @Autowired
    private EventListGroupDao eventListGroupDao;


    @Override
    public List<EventListGroup> findAllByEventId(String eventId) {
        return eventListGroupDao.findAllByEventId(eventId);
    }
}
