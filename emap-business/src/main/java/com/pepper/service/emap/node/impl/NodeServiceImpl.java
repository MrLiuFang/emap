package com.pepper.service.emap.node.impl;

import java.util.HashMap;
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
	public Pager<Node> findNavigator(Pager<Node> pager,String code,String name,String source,String sourceCode,String mapId,String nodeTypeId,String siteId,String buildId,String floor) {
		StringBuffer jpql = new StringBuffer(" select n from Node n join Map m on n.mapId = m.id join BuildingInfo b on m.buildId = b.id join SiteInfo s on s.id = b.siteInfoId where 1=1 ");
		Map<String,Object> searchParameter = new HashMap<String, Object>();
		if(StringUtils.hasText(code)) {
			jpql.append(" and n.code  like :code");
			searchParameter.put("code", "%"+code+"%");
		}
		if(StringUtils.hasText(name)) {
			jpql.append(" and n.name  like :name");
			searchParameter.put("name", "%"+name+"%");
		}
		if(StringUtils.hasText(source)) {
			jpql.append(" and n.source  like :source");
			searchParameter.put("source", "%"+source+"%");
		}
		if(StringUtils.hasText(sourceCode)) {
			jpql.append(" and n.sourceCode  like :sourceCode");
			searchParameter.put("sourceCode", "%"+sourceCode+"%");
		}
		if(StringUtils.hasText(mapId)) {
			jpql.append(" and n.mapId = :mapId");
			searchParameter.put("mapId", mapId);
		}
		if(StringUtils.hasText(nodeTypeId)) {
			jpql.append(" and n.nodeTypeId = :nodeTypeId");
			searchParameter.put("nodeTypeId", nodeTypeId);
		}
		if(StringUtils.hasText(buildId)) {
			jpql.append(" and m.buildId = :buildId");
			searchParameter.put("buildId", buildId);
		}
		if(StringUtils.hasText(siteId)) {
			jpql.append(" and s.id = :siteId");
			searchParameter.put("siteId", siteId);
		}
		if(StringUtils.hasText(floor)) {
			jpql.append(" and m.floor like :floor");
			searchParameter.put("floor", "%"+floor+"%");
		}
		return nodeDao.findNavigator(pager, jpql.toString(),searchParameter );
	}

	@Override
	public Node findBySourceCode(String sourceCode) {
		return nodeDao.findBySourceCode(sourceCode);
	}

	

}
