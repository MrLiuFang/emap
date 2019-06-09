package com.pepper.dao.emap.event;

import java.util.List;

import com.pepper.core.base.BaseDao;
import com.pepper.model.emap.event.HelpList;

public interface HelpListDao extends BaseDao<HelpList> {

	/**
	 * 
	 * @param id
	 * @return
	 */
	public List<HelpList> findByNodeTypeId(String nodeTypeId) ;
	
	public List<HelpList> findByNodeTypeIdAndWarningLevelGreaterThanEqual(String nodeTypeId,int warningLevel) ;
	
	public List<HelpList> findByNodeTypeIdAndWarningLevel(String nodeTypeId,int warningLevel) ;
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public List<HelpList> findByNodeTypeIdAndIdIn(String nodeTypeId, String... id) ;
}
