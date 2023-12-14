package com.happotech.motan.core.filter;

import com.alibaba.fastjson.JSONObject;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.core.extension.Activation;
import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.filter.Filter;
import com.weibo.api.motan.rpc.Caller;
import com.weibo.api.motan.rpc.DefaultResponse;
import com.weibo.api.motan.rpc.Provider;
import com.weibo.api.motan.rpc.Referer;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.serialize.DeserializableObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @Description motan filter
 * @Author zz
 * @Date 2020/5/19 10:08
 */
@SpiMeta(name = "happotech-string-filter")
@Activation(sequence = 98,key = {MotanConstants.NODE_TYPE_REFERER, MotanConstants.NODE_TYPE_SERVICE})
public class StringFilter implements Filter {

    private final static Logger log = LogManager.getLogger(StringFilter.class);


    @Override
    public Response filter(Caller<?> caller, Request request) {

        Response response = caller.call(request);
        doDecorateResponse(caller,response,request);
        return response;
    }

    private Boolean isStringResult(Request request) {
        Method method;
        try {
            Class<?> clz = Class.forName(request.getInterfaceName());
            String[] paramDescs = StringUtils.split(request.getParamtersDesc(), ",");
            Boolean isParamEmpty = ArrayUtils.isEmpty(paramDescs)||"void".equals(paramDescs[0]);
            Class<?>[] argTypes = new Class<?>[isParamEmpty ? 0 : paramDescs.length];
            if (!isParamEmpty){
                for (int i = 0; i < paramDescs.length; i++) {
                    argTypes[i] = Class.forName(String.valueOf(paramDescs[i]));
                }
            }

            method = clz.getMethod(request.getMethodName(),argTypes);
        } catch (ClassNotFoundException e) {
            log.error("判断服务返回值类型失败",e);
            return false;
        } catch (NoSuchMethodException e) {
            log.error("判断服务返回值类型失败",e);
            return false;
        }
        return method.getReturnType() == String.class;
    }

    private void doDecorateResponse(Caller<?> caller, Response response, Request request){
        if (!(response instanceof DefaultResponse)){
            return;
        }
        DefaultResponse resp = (DefaultResponse) response;
        if(caller instanceof Provider){
            if(!isStringResult(request)) {
                doDecorateProvider(resp);
            }
        }else if (caller instanceof Referer){
            doDecorateReferer(resp,request);
        }
    }

    /**
     * 获取返回值类型
     * TODO 后续可以考虑对性能优化，进行缓存
     * @param request
     * @return
     */
    private Type getReturnType(Request request){
        Method method;
        try {
            Class<?> clz = Class.forName(request.getInterfaceName());
            String[] paramDescs = StringUtils.split(request.getParamtersDesc(), ",");
            Boolean isParamEmpty = ArrayUtils.isEmpty(paramDescs)||"void".equals(paramDescs[0]);
            Class<?>[] argTypes = new Class<?>[isParamEmpty ? 0 : paramDescs.length];
            if (!isParamEmpty){
                for (int i = 0; i < paramDescs.length; i++) {
                    argTypes[i] = Class.forName(String.valueOf(paramDescs[i]));
                }
            }
            method = clz.getMethod( request.getMethodName(), argTypes);
            return method.getAnnotatedReturnType().getType();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    /**
     * 对motan客户端获取的结果集进行反序列化
     * @param response
     */
    private void doDecorateReferer(DefaultResponse response, Request request) {
        Type returnType=getReturnType(request);
        if(returnType==null) return;

        Object value = response.getValue();
        //如果返回值为motan序列化后的值，则先进行反序列化解析
        if (value instanceof DeserializableObject){
            try {
                value = ((DeserializableObject) value).deserialize(Object.class);
            } catch (IOException e) {
                log.error("对象反序列化失败", e);
            }
        }
        //如果返回值不为空，则进行JSON反序列化
        if (null != value){
            //当前返回值类型为非泛型对象
            if (returnType instanceof Class<?>){
                Class<?> typeClz = (Class) returnType;
                //数组类型返回值
                if (typeClz.isArray()){
                    value = JSONObject.parseArray(value.toString(), typeClz);
                }else{
                    //对象类型
                    value = JSONObject.parseObject(value.toString(), typeClz);
                }
            }else if (returnType instanceof ParameterizedType){
                //当前返回值类型为泛型对象
                //当前返回值类型为集合类型
                Class<?> type = (Class<?>) ((ParameterizedType) returnType).getRawType();
                if (Collection.class.isAssignableFrom(type)){
                    Type[] actualTypes = ((ParameterizedType) returnType).getActualTypeArguments();
                    value = JSONObject.parseArray(value.toString(), ((Class<?>)actualTypes[0]));
                }else{
                    //对象类型
                    value = JSONObject.parseObject(value.toString(), returnType);
                }
            }
        }
        response.setValue(value);
    }

    /**
     * 对motan服务端返回的结果集进行序列化
     * @param response
     */
    private void doDecorateProvider(DefaultResponse response) {
        Object value = response.getValue();
        response.setValue(JSONObject.toJSONString(value));
    }

}
