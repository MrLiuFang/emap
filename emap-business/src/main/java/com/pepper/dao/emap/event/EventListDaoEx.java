package com.pepper.dao.emap.event;

import java.util.List;

import com.pepper.core.Pager;
import com.pepper.model.emap.event.EventList;
import com.pepper.model.emap.event.HelpList;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface EventListDaoEx {
	
	/**
	 * 
	 * @param pager
	 * @param warningLevel
	 * @return
	 */
	public Pager<EventList> List(Pager<EventList> pager, Boolean isUrgen);
	
	/**
	 * 获取我已转移的事件
	 * @param pager
	 * @return
	 */
	public Pager<EventList> transferList(Pager<EventList> pager,String dispatchFrom);
	

}
