package com.happotech.motan.core.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * motan 网关转发util
 * @author songxulin
 * @date 2021/4/14
 */
@Component
public class MotanRedirectUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static Object getMotanBean(Class clazz){
        return applicationContext.getBean(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static JSONObject requestRun(Object iActMotanService,String param,String ApiUrlPrefix,String redirectMethod) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        param = getParam(param,ApiUrlPrefix,redirectMethod);
        Method run = iActMotanService.getClass().getMethod("run", String.class);
        return JSONObject.parseObject(run.invoke(iActMotanService,param).toString());
    }

    public static String responseRun(String param,Class clazz) throws Exception {
        JSONObject parseObject = JSONObject.parseObject(param);
        String redirectMethod = parseObject.getString("redirectMethod");
        String redirectClazz = parseObject.getString("redirectClazz");
        if(clazz.getName().equals(redirectClazz)&&"run".equals(redirectMethod)){
            throw new Exception("run 方法不允许重定向");
        }
        Class<?> aClass = Class.forName(redirectClazz);
        Object motanBean = MotanRedirectUtil.getMotanBean(aClass);
        Method method = aClass.getMethod(redirectMethod, String.class);
        Object invoke = method.invoke(motanBean, param);
        if(invoke instanceof String){
            return (String) invoke;
        }
        return JSONObject.toJSONString(invoke);
    }

    public static String getParam(String body,String ApiUrlPrefix,String redirectMethod){
        JSONObject param = JSONObject.parseObject(body);
        param.put("redirectMethod",redirectMethod);
        param.put("redirectClazz",ApiUrlPrefix.replaceAll("/",".").substring(1,ApiUrlPrefix.length()-1));
        return param.toJSONString();
    }


}
