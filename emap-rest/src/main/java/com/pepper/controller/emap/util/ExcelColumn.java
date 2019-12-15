package com.pepper.controller.emap.util;

public class ExcelColumn {

	private String name;
	
	private String key;
	
	
	
	public ExcelColumn(String name, String key) {
		super();
		this.name = name;
		this.key = key;
	}

	public static ExcelColumn build(final String name,final String key) {
		return new ExcelColumn(name,key);
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
	
	
}
