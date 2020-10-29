package com.pepper.controller.emap.util;

public class ExcelColumn {

	private String name;
	
	private String key;

	private Boolean isNotField;

	private String defaultValue;
	
	
	public ExcelColumn(String name, String key,Boolean isNotField, String defaultValue) {
		super();
		this.name = name;
		this.key = key;
		this.isNotField = isNotField;
		this.defaultValue = defaultValue;
	}

	public static ExcelColumn build(final String name,final String key) {
		return new ExcelColumn(name,key,null,null);
	}

	public static ExcelColumn build(String name, Boolean isNotField, String defaultValue) {
		return new ExcelColumn(name,null,isNotField,defaultValue);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Boolean getNotField() {
		return isNotField;
	}

	public void setNotField(Boolean notField) {
		isNotField = notField;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
