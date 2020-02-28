package com.pepper.model.emap.vo;

import com.pepper.model.emap.node.NodeClassify;
import com.pepper.model.emap.node.NodeType;

/**
 * 
 * @author Mr.Liu
 *
 */
public class NodeTypeVo extends NodeType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1904626780038634972L;

	
	private String workingIconUrl;
	
	private String stopIconUrl;
	
	private String processingIconUrl;

	private com.pepper.model.emap.node.NodeClassify nodeClassify;

	public NodeClassify getNodeClassify() {
		return nodeClassify;
	}

	public void setNodeClassify(NodeClassify nodeClassify) {
		this.nodeClassify = nodeClassify;
	}

	public String getWorkingIconUrl() {
		return workingIconUrl;
	}

	public void setWorkingIconUrl(String workingIconUrl) {
		this.workingIconUrl = workingIconUrl;
	}

	public String getStopIconUrl() {
		return stopIconUrl;
	}

	public void setStopIconUrl(String stopIconUrl) {
		this.stopIconUrl = stopIconUrl;
	}

	public String getProcessingIconUrl() {
		return processingIconUrl;
	}

	public void setProcessingIconUrl(String processingIconUrl) {
		this.processingIconUrl = processingIconUrl;
	}
	
	
}
