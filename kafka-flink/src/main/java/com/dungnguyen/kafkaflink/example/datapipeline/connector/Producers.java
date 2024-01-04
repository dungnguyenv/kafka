package com.dungnguyen.kafkaflink.example.datapipeline.connector;

import com.dungnguyen.kafkaflink.example.datapipeline.model.Backup;
import com.dungnguyen.kafkaflink.example.datapipeline.schema.BackupSerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

public class Producers {

    public static FlinkKafkaProducer<String> createStringProducer(String topic, String kafkaAddress) {
        return new FlinkKafkaProducer<>(kafkaAddress, topic, new SimpleStringSchema());
    }

    public static FlinkKafkaProducer<Backup> createBackupProducer(String topic, String kafkaAddress) {
        return new FlinkKafkaProducer<Backup>(kafkaAddress, topic, new BackupSerializationSchema());
    }
}