package com.pepper.controller.emap.util;

import java.util.Map;

public class ExcelColumn {

	private String name;
	
	private String key;

	private Boolean isNotField;

	private String defaultValue;

	private Map<String,String> fieldMapping;
	
	
	public ExcelColumn(String name, String key,Boolean isNotField, String defaultValue,Map<String,String> fieldMapping) {
		super();
		this.name = name;
		this.key = key;
		this.isNotField = isNotField;
		this.defaultValue = defaultValue;
		this.fieldMapping = fieldMapping;
	}

	public static ExcelColumn build( String name, String key) {
		return new ExcelColumn(name,key,null,null,null);
	}

	public static ExcelColumn build( String name, String key,Map<String,String> fieldMapping) {
		return new ExcelColumn(name,key,null,null,fieldMapping);
	}

	public static ExcelColumn build(String name, Boolean isNotField, String defaultValue) {
		return new ExcelColumn(name,null,isNotField,defaultValue,null);
	}

	public static ExcelColumn build(String name, Boolean isNotField, String defaultValue,Map<String,String> fieldMapping) {
		return new ExcelColumn(name,null,isNotField,defaultValue,fieldMapping);
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

	public Map<String, String> getFieldMapping() {
		return fieldMapping;
	}

	public void setFieldMapping(Map<String, String> fieldMapping) {
		this.fieldMapping = fieldMapping;
	}
}
