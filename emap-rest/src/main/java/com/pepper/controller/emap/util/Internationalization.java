package com.pepper.controller.emap.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.pepper.core.base.ICurrentUser;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.util.SpringContextUtil;
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
		if (language.toLowerCase().equals("zh")) {
			return  zhtw.getProperty(String.valueOf(code), "");
		} else if (language.toLowerCase().equals("en")) {
			return  enus.getProperty(String.valueOf(code), "");
		} else {
			return zhtw.getProperty(String.valueOf(code), "");
		}
	}

	@SuppressWarnings("unchecked")
	private static String getLanguage() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String language = "";
		if (request.getParameter("language") != null) {
			language = request.getParameter("language").toString();
		}
		if (!StringUtils.hasText(language) && request.getHeader("language") != null) {
			language = request.getHeader("language").toString();
		}
		if (!StringUtils.hasText(language)){
			RedisTemplate<String, String> redisTemplate = (RedisTemplate<String, String>) SpringContextUtil.getBean("redisTemplate");
			ICurrentUser iCurrentUser = (ICurrentUser) SpringContextUtil.getBean("currentUserUtil");
			AdminUser adminUser =  (AdminUser)iCurrentUser.getCurrentUser();
			if(adminUser!=null) {
				language = redisTemplate.opsForValue().get(adminUser.getId()+"_language");
			}
		}
		if (!StringUtils.hasText(language)){
			language = "zh";
		}
		return language;
	}

}
