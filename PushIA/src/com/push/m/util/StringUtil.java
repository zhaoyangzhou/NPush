package com.push.m.util;

import java.util.Map;

import com.google.gson.Gson;

public class StringUtil {
	
	public static String mapToJson(Map map) {
		if(map != null) {
			Gson gson = new Gson();
			return gson.toJson(map);
		}
		return null;
	}
}
