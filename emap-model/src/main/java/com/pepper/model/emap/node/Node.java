package com.pepper.model.emap.node;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.pepper.common.emuns.Status;
import com.pepper.core.base.BaseModel;

@Entity()
@Table(name = "t_node_info")
@DynamicUpdate(true)
@DynamicInsert
public class Node extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2480337669147928582L;

	@Column(name = "code", unique = true, nullable = false)
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "source_type")
	private String sourceType;

	@Column(name = "source")
	private String source;

	@Column(name = "source_code", unique = true, nullable = false)
	private String sourceCode;

	@Column(name = "map_id")
	private String mapId;

	@Column(name = "node_type_id", nullable = false)
	private String nodeTypeId;

	@Column(name = "parent_node")
	private String parentNode;

	@Column(name = "external_link")
	private String externalLink;

	@Column(name = "warning_level")
	private Integer warningLevel;

	@Column(name = "x")
	private String x;

	@Column(name = "y")
	private String y;

	@Column(name = "longitude")
	private String longitude;

	@Column(name = "latitude")
	private String latitude;

	@Column(name = "ip")
	private String ip;

	@Column(name = "has_ptz")
	private Boolean hasPtz;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "password")
	private String password;

	@Column(name = "system_id")
	private String systemID;

	@Column(name = "windows_user")
	private String windowsUser;

	@Column(name = "windows_pass")
	private String windowsPass;

	@Column(name = "domain_name")
	private String domainName;

	@Column(name = "pane_id")
	private String paneId;

	@Column(name = "pane_ip")
	private String paneIp;

	@Column(name = "reader_id")
	private String readerId;

	@Column(name = "reader_io")
	private String readerIo;

	@Column(name = "status")
	private Status status;

	@Column(name = "remark")
	private String remark;

	@Column(name = "extra",length = 1000)
	private String extra;

	@Column(name = "is_door")
	private Boolean isDoor;

	@Column(name = "is_camera")
	private Boolean isCamera;

	@Column(name = "tile_type",columnDefinition = "int(10) default 512")
	private Integer tileType;

	public Boolean getIsDoor() {
		return isDoor;
	}

	public void setIsDoor(Boolean isDoor) {
		this.isDoor = isDoor;
	}

	public Boolean getIsCamera() {
		return isCamera;
	}

	public void setIsCamera(Boolean isCamera) {
		this.isCamera = isCamera;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public String getNodeTypeId() {
		return nodeTypeId;
	}

	public void setNodeTypeId(String nodeTypeId) {
		this.nodeTypeId = nodeTypeId;
	}

	public String getParentNode() {
		return parentNode;
	}

	public void setParentNode(String parentNode) {
		this.parentNode = parentNode;
	}

	public String getExternalLink() {
		return externalLink;
	}

	public void setExternalLink(String externalLink) {
		this.externalLink = externalLink;
	}

	public Integer getWarningLevel() {
		return warningLevel;
	}

	public void setWarningLevel(Integer warningLevel) {
		this.warningLevel = warningLevel;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Boolean getHasPtz() {
		return hasPtz;
	}

	public void setHasPtz(Boolean hasPtz) {
		this.hasPtz = hasPtz;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSystemID() {
		return systemID;
	}

	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}

	public String getWindowsUser() {
		return windowsUser;
	}

	public void setWindowsUser(String windowsUser) {
		this.windowsUser = windowsUser;
	}

	public String getWindowsPass() {
		return windowsPass;
	}

	public void setWindowsPass(String windowsPass) {
		this.windowsPass = windowsPass;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getPaneId() {
		return paneId;
	}

	public void setPaneId(String paneId) {
		this.paneId = paneId;
	}

	public String getPaneIp() {
		return paneIp;
	}

	public void setPaneIp(String paneIp) {
		this.paneIp = paneIp;
	}

	public String getReaderId() {
		return readerId;
	}

	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}

	public String getReaderIo() {
		return readerIo;
	}

	public void setReaderIo(String readerIo) {
		this.readerIo = readerIo;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getTileType() {
		return tileType;
	}

	public void setTileType(Integer tileType) {
		this.tileType = tileType;
	}
}
