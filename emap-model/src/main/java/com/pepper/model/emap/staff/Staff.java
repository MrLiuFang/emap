package com.pepper.model.emap.staff;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import com.pepper.common.emuns.Gender;
import com.pepper.core.base.BaseModel;
import com.pepper.core.validator.Validator.Insert;
import com.pepper.core.validator.Validator.Update;

@Entity()
@Table(name = "t_staff")
@DynamicUpdate(true)
public class Staff extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8054236127746403241L;
	
	@Column(name = "name")
	private String name;

	/**
	 * 邮箱
	 */
	@Column(name = "email")
	private String email;


	@Column(name = "password")
	private String password;

	@Column(name = "need_change_password")
	private Boolean needChangePassword;
	
	@Column(name = "is_available")
	private Boolean isAvailable;
	
	@Column(name = "available_time")
	private Date availableTime;
	
	@Column(name = "site_id")
	private String siteId;
	
	@Column(name = "password_never_expire")
	private Boolean passwordNeverExpire;
	
	@Column(name = "last_login_time")
	private Date lastLoginTime;
	
	@Column(name = "id_card" ,unique= true)
	private String idCard;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getNeedChangePassword() {
		return needChangePassword;
	}

	public void setNeedChangePassword(Boolean needChangePassword) {
		this.needChangePassword = needChangePassword;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public Date getAvailableTime() {
		return availableTime;
	}

	public void setAvailableTime(Date availableTime) {
		this.availableTime = availableTime;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public Boolean getPasswordNeverExpire() {
		return passwordNeverExpire;
	}

	public void setPasswordNeverExpire(Boolean passwordNeverExpire) {
		this.passwordNeverExpire = passwordNeverExpire;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	
	
}
