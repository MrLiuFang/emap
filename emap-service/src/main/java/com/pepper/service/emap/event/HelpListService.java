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
	public List<HelpList> findByNodeTypeId(String nodeTypeId);
	
	List<HelpList> findByNodeTypeIdAndWarningLevelGreaterThanEqual(String nodeTypeId,int warningLevel);
	
	public List<HelpList> findByNodeTypeIdAndWarningLevel(String nodeTypeId,int warningLevel) ;
	
	public List<HelpList> findByNodeTypeIdAndWarningLevelLessThanEqual(String nodeTypeId,int warningLevel);
	/**
	 * 
	 * @param id
	 * @return
	 */
	public List<HelpList> findByNodeTypeIdAndIdIn(String nodeTypeId, String... id) ;
}
