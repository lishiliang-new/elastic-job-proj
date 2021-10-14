package com.lishiliang.core.configuration;

import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.registry.zookeeper.ZookeeperRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 多注册中心之Zookeeper注册中心配置
 *  可通过
 *  @ComponentScan(excludeFilters={@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ZookeeperRegistryClass.class)})
 *  设置进行排除
 */
@Configuration
@ConditionalOnClass(ZookeeperRegistry.class)
public class ZookeeperRegistryClass {
    @Bean(name = "zookeeper-registry")
    @ConditionalOnClass(ZookeeperRegistry.class)
    public RegistryConfig zookeeperRegistryConfig(@Value("${dubbo.registry.zookeeper.address}") String zookeeperAddress) {
        RegistryConfig registry = new RegistryConfig();
        registry.setId("zookeeper_registry");
        registry.setAddress(zookeeperAddress);
        registry.setDefault(true);
        return registry;
    }
}