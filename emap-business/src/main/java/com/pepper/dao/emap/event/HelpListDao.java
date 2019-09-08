package com.pepper.dao.emap.event;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
	
	public Page<HelpList> findByNodeTypeIdAndWarningLevelLessThanEqual(String nodeTypeId,int warningLevel,Pageable pageable) ;
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public List<HelpList> findByNodeTypeIdAndIdIn(String nodeTypeId, String... id) ;
	
	public HelpList findOneByCode(String code) ;
}
