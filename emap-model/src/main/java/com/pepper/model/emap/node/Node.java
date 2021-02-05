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

	@Column(name = "line_path",length = 500)
	private String line;

	@Column(name = "port")
	private Integer port;

	@Column(name = "in_port")
	private Integer inPort;

	@Column(name = "out_port")
	private Integer outPort;

	@Column(name = "out_is_on")
	private Boolean outIsOn;

	@Column(name = "icon")
	private String icon;

	@Column(name = "alarm_icon")
	private String alarmIcon;

	@Column(name = "problem_icon")
	private String problemIcon;

	@Column(name = "disabled_icon")
	private String disabledIcon;

	@Column(name = "is_zone")
	private Boolean isZone;

	@Column(name = "is_out")
	private Boolean isOut;

	@Column(name = "cam_ip")
	private String camIp;

	@Column(name = "cam_port")
	private String camPort;

	@Column(name = "cam_cmd")
	private String camCmd;

	@Column(name = "status_university")
	private Integer statusUniversity;

	public Integer getStatusUniversity() {
		return statusUniversity;
	}

	public void setStatusUniversity(Integer statusUniversity) {
		this.statusUniversity = statusUniversity;
	}

	public String getCamIp() {
		return camIp;
	}

	public void setCamIp(String camIp) {
		this.camIp = camIp;
	}

	public String getCamPort() {
		return camPort;
	}

	public void setCamPort(String camPort) {
		this.camPort = camPort;
	}

	public String getCamCmd() {
		return camCmd;
	}

	public void setCamCmd(String camCmd) {
		this.camCmd = camCmd;
	}

	public Boolean getZone() {
		return isZone;
	}

	public void setZone(Boolean zone) {
		isZone = zone;
	}

	public Boolean getOut() {
		return isOut;
	}

	public void setOut(Boolean out) {
		isOut = out;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getInPort() {
		return inPort;
	}

	public void setInPort(Integer inPort) {
		this.inPort = inPort;
	}

	public Integer getOutPort() {
		return outPort;
	}

	public void setOutPort(Integer outPort) {
		this.outPort = outPort;
	}

	public Boolean getOutIsOn() {
		return outIsOn;
	}

	public void setOutIsOn(Boolean outIsOn) {
		this.outIsOn = outIsOn;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getAlarmIcon() {
		return alarmIcon;
	}

	public void setAlarmIcon(String alarmIcon) {
		this.alarmIcon = alarmIcon;
	}

	public String getProblemIcon() {
		return problemIcon;
	}

	public void setProblemIcon(String problemIcon) {
		this.problemIcon = problemIcon;
	}

	public String getDisabledIcon() {
		return disabledIcon;
	}

	public void setDisabledIcon(String disabledIcon) {
		this.disabledIcon = disabledIcon;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

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

}
