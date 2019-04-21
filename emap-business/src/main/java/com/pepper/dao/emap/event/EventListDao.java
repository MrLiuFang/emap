package com.pepper.dao.emap.event;

import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.event.EventList;

public interface EventListDao extends BaseDao<EventList> {

	/**
	 * 查询未处理的事件
	 * @param status
	 * @param status1
	 * @return
	 */
	public List<EventList> findByStatusOrStatus(String status,String status1);
}
