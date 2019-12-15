package com.pepper.service.emap.event.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.pepper.core.Pager;
import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.event.HelpListDao;
import com.pepper.model.emap.event.HelpList;
import com.pepper.service.emap.event.HelpListService;

@Service(interfaceClass=HelpListService.class)
public class HelpListServiceImpl extends BaseServiceImpl<HelpList> implements HelpListService {

	@Resource
	private HelpListDao helpListDao;

	@Override
	public List<HelpList> findByNodeTypeId(String nodeTypeId) {
		return helpListDao.findByNodeTypeId(nodeTypeId);
	}

	@Override
	public List<HelpList> findByNodeTypeIdAndIdIn(String nodeTypeId, String... id) {
		return helpListDao.findByNodeTypeIdAndIdIn(nodeTypeId, id);
	}

	@Override
	public List<HelpList> findByNodeTypeIdAndWarningLevelGreaterThanEqual(String nodeTypeId, int warningLevel) {
		return helpListDao.findByNodeTypeIdAndWarningLevelGreaterThanEqual(nodeTypeId, warningLevel);
	}

	@Override
	public List<HelpList> findByNodeTypeIdAndWarningLevel(String nodeTypeId, int warningLevel) {
		return helpListDao.findByNodeTypeIdAndWarningLevel(nodeTypeId, warningLevel);
	}

	@Override
	public Pager<HelpList> findByNodeTypeIdAndWarningLevelLessThanEqual(String nodeTypeId, int warningLevel,Pager<HelpList> pager) {
		Pageable pageable = PageRequest.of(pager.getPageNo()-1, pager.getPageSize());
		Page<HelpList> page = helpListDao.findByNodeTypeIdAndWarningLevelLessThanEqual(nodeTypeId, warningLevel,pageable);
		pager.setResults(page.getContent());
		pager.setTotalRow(Long.valueOf(page.getTotalElements()));
		return pager;
	}

	@Override
	public HelpList findByCode(String code) {
<<<<<<< HEAD
		return helpListDao.findOneByCode(code);
=======
		return helpListDao.findFirstByCode(code);
>>>>>>> refs/heads/master
	}
}
