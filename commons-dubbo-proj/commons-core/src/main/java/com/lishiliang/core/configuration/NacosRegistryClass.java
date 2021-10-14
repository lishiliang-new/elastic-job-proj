package com.lishiliang.core.configuration;

import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.registry.nacos.NacosRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 多注册中心之Zookeeper注册中心配置
 *  可通过
 *  @ComponentScan(excludeFilters={@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = NacosRegistryClass.class)})
 *  设置进行排除
 */
@Configuration
@ConditionalOnClass(NacosRegistry.class)
public class NacosRegistryClass {
    @Bean(name = "nacos-registry")
    @ConditionalOnClass(NacosRegistry.class)
    public RegistryConfig nacosRegistryConfig(@Value("${dubbo.registry.nacos.address}") String nacosAddress) {
        RegistryConfig registry = new RegistryConfig();
        registry.setId("nacos_registry");
        registry.setAddress(nacosAddress);
        registry.setDefault(false);
        return registry;
    }
}