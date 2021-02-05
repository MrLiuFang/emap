package com.pepper.service.emap.event;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.event.EventListGroup;

import java.util.List;

/**
 * @description:
 * @author: 雄大
 * @create: 2021-02-04 17:16
 */
public interface EventListGroupService extends BaseService<EventListGroup> {

    public List<EventListGroup> findAllByEventId(String eventId);

    public List<EventListGroup> findAllByNodeIdAndStatusNot(String nodeId,String status);
}
