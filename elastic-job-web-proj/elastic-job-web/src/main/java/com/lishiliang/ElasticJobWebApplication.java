package com.lishiliang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan(basePackages = "com.lishiliang.*"
        //排除Swagger
//        ,excludeFilters={@ComponentScan.Filter(classes = EnableSwagger2.class)}
)
public class ElasticJobWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticJobWebApplication.class, args);
    }

}
