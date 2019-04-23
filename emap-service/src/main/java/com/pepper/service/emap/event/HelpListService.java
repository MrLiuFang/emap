package com.pepper.service.emap.event;

import java.util.List;

import com.pepper.core.base.BaseService;
import com.pepper.model.emap.event.HelpList;

/**
 * 
 * @author Mr.Liu
 *
 */
public interface HelpListService extends BaseService<HelpList> {

	/**
	 * 
	 * @param id
	 * @return
	 */
	public List<HelpList> findByNodeTypeId(String id);
}
