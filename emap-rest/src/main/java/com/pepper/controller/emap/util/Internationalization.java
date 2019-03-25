package com.pepper.controller.emap.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sun.istack.NotNull;

/**
 * 
 * @author Mr.Liu
 *
 */
public class Internationalization {

	private static Properties enus = new Properties();

	private static Properties zhtw = new Properties();

	static {
		
		try {
			InputStream in = Internationalization.class.getResourceAsStream("/en-us.properties");
			InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
			enus.load(inputStreamReader);
			
			in = Internationalization.class.getResourceAsStream("/zh-tw.properties");
			inputStreamReader = new InputStreamReader(in, "UTF-8");
			zhtw.load(inputStreamReader);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public static String getMessageInternationalization(@NotNull final Integer code) {
		String language = getLanguage();
		if (language.toLowerCase().equals("zh-tw")) {
			return  zhtw.getProperty(String.valueOf(code), "");
		} else if (language.toLowerCase().equals("en-us")) {
			return  enus.getProperty(String.valueOf(code), "");
		} else {
			return zhtw.getProperty(String.valueOf(code), "");
		}
	}

	private static String getLanguage() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		if (request.getParameter("language") != null) {
			return request.getParameter("language").toString();
		}
		if (request.getHeader("language") != null) {
			return request.getHeader("language").toString();
		}
		return "zh-tw";
	}

}
