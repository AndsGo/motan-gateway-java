package com.happotech.client.configuration;


import com.weibo.api.motan.config.springsupport.AnnotationBean;
import com.weibo.api.motan.config.springsupport.ProtocolConfigBean;
import com.weibo.api.motan.config.springsupport.RegistryConfigBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Description: 
 *  motan client端和server端共用配置信息
 *  包括注册中心配置、协议配置
 * @author: rbkevin.xu 
 * @version: 2018年11月28日 下午4:24:45
 */
@Configuration
public class MotanConfiguration {

    /**
     * @Description: 需要一个返回类型为AnnotationBean的bean定义，这个用来扫描包来解析motan的注解进行暴露服务或引用服务
     * @author: rbkevin.xu
     * @return
     * @version: 2018年11月28日 下午4:25:07
     */
    @Bean
    public AnnotationBean motanAnnotationBean() {
        return new AnnotationBean();
    }

    /**
     * @Description: 注册中心配置 使用不同注册中心需要依赖对应的jar包。如果不使用注册中心，可以把check属性改为false，忽略注册失败。
     * @author: rbkevin.xu
     * @return
     * @version: 2018年11月28日 下午4:25:19
     */
    @Bean(name = "zkRegistry")
    @ConfigurationProperties(prefix = "motan.registry")
    public RegistryConfigBean registryConfig() {
        return new RegistryConfigBean();
    }

    /**
     * @Description: 协议配置。为防止多个业务配置冲突，推荐使用id表示具体协议。
     * @author: rbkevin.xu
     * @return
     * @version: 2018年11月28日 下午4:25:31
     */
    @Bean(name = "motan2")
    @ConfigurationProperties(prefix = "motan.protocol")
    public ProtocolConfigBean protocolConfig1() {
        return new ProtocolConfigBean();
    }
}
