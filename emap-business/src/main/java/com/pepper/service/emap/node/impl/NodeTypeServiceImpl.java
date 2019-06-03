package com.pepper.service.emap.node.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;

import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.dao.emap.node.NodeTypeDao;
import com.pepper.model.emap.node.NodeType;
import com.pepper.service.emap.node.NodeTypeService;
/**
 * 
 * @author Mr.Liu
 *
 */
@Service(interfaceClass=NodeTypeService.class)
public class NodeTypeServiceImpl extends BaseServiceImpl<NodeType> implements NodeTypeService {
	
	@Resource
	private NodeTypeDao nodeTypeDao;

	@Override
	public NodeType findByCode(String code) {
		return nodeTypeDao.findByCode(code);
	}

	@Override
	public List<NodeType> findByName(String name) {
		return nodeTypeDao.findByName(name);
	}

}
