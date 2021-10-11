package com.lishiliang;

import com.cxytiandi.elasticjob.annotation.EnableElasticJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableElasticJob
public class ElasticJobCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticJobCoreApplication.class, args);
    }

}
