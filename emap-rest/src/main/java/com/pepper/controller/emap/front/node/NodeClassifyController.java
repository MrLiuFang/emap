package com.pepper.controller.emap.front.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pepper.controller.emap.core.ResultData;
import com.pepper.controller.emap.util.Internationalization;
import com.pepper.core.Pager;
import com.pepper.core.base.BaseController;
import com.pepper.core.base.impl.BaseControllerImpl;
import com.pepper.core.constant.SearchConstant;
import com.pepper.model.emap.node.NodeClassify;
import com.pepper.model.emap.site.SiteInfo;
import com.pepper.service.authentication.aop.Authorize;
import com.pepper.service.emap.log.SystemLogService;
import com.pepper.service.emap.node.NodeClassifyService;
import com.pepper.util.MapToBeanUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

@Controller()
@RequestMapping(value = "/front/node/classify")
public class NodeClassifyController  extends BaseControllerImpl implements BaseController {
    @Reference
    private NodeClassifyService nodeClassifyService;

    @Reference
    private SystemLogService systemLogService;

    private Pager<NodeClassify> getPager(String code, String name,Boolean isExport) {
        Pager<NodeClassify> pager = new Pager<NodeClassify>();
        if (isExport) {
            pager.setPageNo(1);
            pager.setPageSize(Integer.MAX_VALUE);
        }
        if(StringUtils.hasText(code)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_code",code );
        }
        if(StringUtils.hasText(name)) {
            pager.getJpqlParameter().setSearchParameter(SearchConstant.LIKE+"_name",name );
        }

        pager = nodeClassifyService.findNavigator(pager);

        pager.setData("nodeClassify",pager.getResults());
        pager.setResults(null);
        return pager;
    }

    @RequestMapping(value = "/list")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object list(String code,String name) {

//		systemLogService.log("get site list", this.request.getRequestURL().toString());
        return getPager(code, name, false);
    }

    @RequestMapping(value = "/add")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object add(@RequestBody NodeClassify nodeClassify) {
        ResultData resultData = new ResultData();

        if(nodeClassifyService.findNodeClassify(nodeClassify.getCode())!=null) {
            resultData.setCode(2000001);
            resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
            return resultData;
        }

        nodeClassifyService.save(nodeClassify);
        systemLogService.log("site node classify", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/update")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object update(@RequestBody NodeClassify nodeClassify) {
        ResultData resultData = new ResultData();

        NodeClassify oldNodeClassify = nodeClassifyService.findNodeClassify(nodeClassify.getCode());
        if(oldNodeClassify!=null && oldNodeClassify.getCode()!=null&&nodeClassify.getCode()!=null) {
            if(!nodeClassify.getId().equals(oldNodeClassify.getId())){
                resultData.setCode(2000001);
                resultData.setMessage(Internationalization.getMessageInternationalization(2000001));
                return resultData;
            }
        }
        nodeClassifyService.update(nodeClassify);
        systemLogService.log("node classify update", this.request.getRequestURL().toString());
        return resultData;
    }

    @RequestMapping(value = "/toEdit")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object toEdit(String id) {
        ResultData resultData = new ResultData();
        resultData.setData("nodeClassify",nodeClassifyService.findById(id));
        return resultData;
    }

    @RequestMapping(value = "/delete")
    @Authorize(authorizeResources = false)
    @ResponseBody
    public Object delete(@RequestBody String str) throws IOException {
        ResultData resultData = new ResultData();
        if(!StringUtils.hasText(str)){
            return resultData;
        }
        JsonNode jsonNode = new ObjectMapper().readTree(str);
        if(!jsonNode.has("id")) {
            return resultData;
        }
        ArrayNode arrayNode = (ArrayNode)jsonNode.get("id");
        for(int i = 0; i <arrayNode.size(); i++) {
            String id = arrayNode.get(i).asText();
            try {
                nodeClassifyService.deleteById(id);
            }catch (Exception e) {
                // TODO: handle exception
            }
        }
        systemLogService.log("node classify delete", this.request.getRequestURL().toString());
        return resultData;
    }
}
