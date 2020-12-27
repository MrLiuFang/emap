package com.pepper.controller.emap.front.data;

import com.pepper.controller.emap.core.ResultData;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.model.console.admin.user.AdminUser;
import com.pepper.service.authentication.aop.Authorize;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.SQLException;

@RestController
@RequestMapping(value = "/front/data")
public class DataController extends BaseControllerImpl implements BaseController {


    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @RequestMapping("/info")
    @Authorize(authorizeResources = false)
    public Object info() throws SQLException {
        ResultData resultData = new ResultData();
        AdminUser user = (AdminUser) this.getCurrentUser();
        url =url.replace("jdbc:mysql://","");
        resultData.setData("dbHost",url.split(":")[0]);
        resultData.setData("dbPort",url.split(":")[1].split("/")[0]);
        resultData.setData("dbName",url.split(":")[1].split("/")[1].split("\\?")[0]);
        resultData.setData("dbUser",username);
        resultData.setData("dbPass",password);
        resultData.setData("isms_user",user.getAccount());
        return resultData;
    }
}
