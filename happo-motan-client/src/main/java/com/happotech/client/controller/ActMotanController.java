package com.happotech.client.controller;

import com.alibaba.fastjson.JSONObject;
import com.happotech.actmgr.motan.service.api.IActMotanService;
import com.happotech.motan.core.util.MotanRedirectUtil;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ActMotanController {
    @MotanReferer
    private IActMotanService iActMotanService;
    //ApiUrlPrefix 为接口类的全路径
    private static final String ApiUrlPrefix = "/com/happotech/actmgr/motan/service/api/IActMgrService/";

    @RequestMapping(ApiUrlPrefix + "{method}")
    @ResponseBody
    public JSONObject urcService(@RequestBody String body, @PathVariable("method") String method, HttpServletRequest request)
            throws Exception {
        return MotanRedirectUtil.requestRun(iActMotanService, body, ApiUrlPrefix, method);
    }
}
