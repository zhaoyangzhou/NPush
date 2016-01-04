package com.push.m.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class PropertyUtil {
	public static String getValue(String fileName, String key) {
		ResourceBundle bundle = ResourceBundle.getBundle(fileName, Locale.getDefault());
		String value = bundle.getString(key);  
		return value;
	}
}
