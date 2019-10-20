package com.pepper.model.emap.enums;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;
import com.pepper.core.IEnum;

public enum Classify implements IEnum {
	DOOR(0, "门"), CAMERA(1, "摄像头"),OTHER(2, "其它");

	private final int key;

	private final String desc;

	private Classify(int key, String desc) {
		this.key = key;
		this.desc = desc;
	}

	@Override
	public Integer getKey() {
		return key;
	}

	@Override
	public String getName() {
		return this.toString();
	}
	
	@Override
	
	public String getDesc(){
		return desc;
	}
	
	@JsonValue
	public Map<String, Object> jsonValue() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key", key);
		map.put("desc", desc);
		map.put("name", getName());
		return map;
	}
	
}
