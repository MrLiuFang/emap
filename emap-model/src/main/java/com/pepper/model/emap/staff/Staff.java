package com.pepper.model.emap.staff;

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

	@Column(name = "position")
	private String position;

	/**
	 * 用户手机
	 */
	@Column(name = "mobile")
	private String mobile;

	/**
	 * 邮箱
	 */
	@Column(name = "email")
	private String email;

	/**
	 * 头像
	 */
	@Column(name = "head_portrait")
	private String headPortrait;

//	/**
//	 * 性别
//	 */
//	@Column(name = "gender")
//	private Gender gender; 

	@Column(name = "department_id")
	private String departmentId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHeadPortrait() {
		return headPortrait;
	}

	public void setHeadPortrait(String headPortrait) {
		this.headPortrait = headPortrait;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	
	
}
