package com.lishiliang;

import com.lishiliang.core.configuration.NacosRegistryConfig;
import com.lishiliang.core.configuration.ZookeeperRegistryConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = "com.lishiliang.*"
        //排除Swagger
//        ,excludeFilters={@ComponentScan.Filter(classes = EnableSwagger2.class)}
        //排除注册中心
//        ,excludeFilters={@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {NacosRegistryConfig.class, ZookeeperRegistryConfig.class})}
)
public class ElasticJobWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticJobWebApplication.class, args);
    }

}
