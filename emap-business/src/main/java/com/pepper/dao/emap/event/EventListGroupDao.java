package com.pepper.dao.emap.event;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.event.EventListGroup;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @description:
 * @author: 雄大
 * @create: 2021-02-04 17:19
 */
public interface EventListGroupDao extends BaseDao<EventListGroup> {

    @Query(" select t1 from EventListGroup t1 where t1.eventGroupId = (select t2.eventGroupId from EventListGroup t2 where t2.eventId = :eventId )")
    public List<EventListGroup> findAllByEventId(String eventId);

    public List<EventListGroup> findAllByNodeGroupCodeAndStatusNot(String nodeGroupCode,String status);

}
