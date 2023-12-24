package com.dungnguyen.kafkaflink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.dungnguyen.kafkaflink.demojava.datapipeline.FlinkDataPipeline.createBackup;

@SpringBootApplication
public class KafkaFlinkApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(KafkaFlinkApplication.class, args);
    }

}
