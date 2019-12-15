package com.pepper.service.emap.node.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.util.StringUtils;

import com.pepper.core.Pager;
import com.pepper.core.base.impl.BaseServiceImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.dao.emap.node.NodeDao;
import com.pepper.model.emap.node.Node;
import com.pepper.service.emap.node.NodeService;

@Service(interfaceClass=NodeService.class)
public class NodeServiceImpl extends BaseServiceImpl<Node> implements NodeService {
	
	@Resource
	private NodeDao nodeDao;

	@Override
	public Pager<Node> findNavigator(Pager<Node> pager,String code,String name,String source,String sourceCode,String mapId,String nodeTypeId,String siteId,String buildId,String floor,String hasXY,String keyWord) {
		return nodeDao.findNavigator(pager, code, name, source, sourceCode, mapId, nodeTypeId, siteId, buildId, floor,hasXY, keyWord);
	}
		

	@Override
	public Node findBySourceCode(String sourceCode) {
		return nodeDao.findFirstBySourceCode(sourceCode);
	}

	@Override
	public List<Node> findByMapIdAndHasEvent(String mapId) {
		return nodeDao.findByMapIdAndHasEvent(mapId);
	}


	@Override
	public Node findByCode(String code) {
		return nodeDao.findFirstByCode(code);
	}


	@Override
	public Node findByName(String name) {
		return nodeDao.findFirstByName(name);
<<<<<<< HEAD
=======
	}
	
	@Override
	public List<Node> findByNameLike(String name) {
		return nodeDao.findByNameLike("%"+name+"%");
>>>>>>> refs/heads/master
	}

	

}
