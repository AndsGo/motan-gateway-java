package com.happotech.client.configuration;

import com.weibo.api.motan.config.springsupport.BasicRefererConfigBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: motan client端配置信息类
 * @author: rbkevin.xu 
 * @version: 2018年11月28日 下午4:21:22
 */
@Configuration
public class MotanClientConfiguration {

    /**
     * @Description: 
     *   1、根据配置文件里motan.client.enabled=true配置是否启用motan client功能
     *        eg:motan.client.enabled=true 启用
     *           motan.client.enabled=false 关闭
     * 2、通用referer基础配置
     * @author: rbkevin.xu
     * @return
     * @version: 2018年11月28日 下午4:21:40
     */
    @ConditionalOnProperty(prefix = "motan.client", value = "enabled", havingValue = "true")
    @Bean(name = "basicRefererConfig")
    @ConfigurationProperties(prefix = "motan.client.basicreferer")
    public BasicRefererConfigBean basicRefererConfigBean() {
        return new BasicRefererConfigBean();
    }

}
