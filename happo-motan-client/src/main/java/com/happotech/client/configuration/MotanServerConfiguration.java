package com.happotech.client.configuration;


import com.weibo.api.motan.config.springsupport.BasicServiceConfigBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: motan server端信息配置
 * @author: rbkevin.xu 
 * @version: 2018年11月28日 下午4:25:52
 */
@Configuration
public class MotanServerConfiguration {


    /**
     * @Description: 
     * 1、根据配置文件里motan.server.enabled=true配置是否启用motan server功能
     *     eg:motan.server.enabled=true 启用
     *        motan.server.enabled=false 关闭
     * 2、通用配置，多个rpc服务使用相同的基础配置. group和module定义具体的服务池。
     * export格式为“protocol id:提供服务的端口
     * @author: rbkevin.xu
     * @return
     * @version: 2018年11月28日 下午4:26:02
     */
    @ConditionalOnProperty(prefix = "motan.server", value = "enabled", havingValue = "true")
    @Bean(name = "serviceBasicConfig")
    @ConfigurationProperties(prefix = "motan.server.basic")
    public BasicServiceConfigBean baseServiceConfig() {
        return new BasicServiceConfigBean();
    }


}
