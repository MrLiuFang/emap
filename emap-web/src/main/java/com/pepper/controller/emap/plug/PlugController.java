package com.pepper.controller.emap.plug;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;

@Controller()
@RequestMapping("/front/plug")
public class PlugController extends BaseControllerImpl implements BaseController {

	@RequestMapping("/view")
	public String view() {
//		this.request.setAttribute("data", this.request.getParameterMap().toString());
		return "plug/plug";
	}
}
