package com.pepper.controller.emap.gateway;

import java.net.URL;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.pepper.core.gateway.AbsRegisterUrl;

/**
 * 注册资源URL（zookeeper）
 * 
 * @author mrliu
 *
 */
@Component("emapWebRegisterUrl")
@Lazy
public class RegisterUrl extends AbsRegisterUrl {

	@Override
	protected URL getCodeSourcePath() {
		return RegisterUrl.class.getProtectionDomain().getCodeSource().getLocation();
	}

}
