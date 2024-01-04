package com.dungnguyen.kafkaflink.example.datapipeline;

import com.dungnguyen.kafkaflink.example.datapipeline.model.Backup;
import com.dungnguyen.kafkaflink.example.datapipeline.model.InputMessage;
import com.dungnguyen.kafkaflink.example.datapipeline.operator.BackupAggregator;
import com.dungnguyen.kafkaflink.example.datapipeline.operator.InputMessageTimestampAssigner;
import com.dungnguyen.kafkaflink.example.datapipeline.operator.WordsCapitalizer;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import static com.dungnguyen.kafkaflink.example.datapipeline.connector.Consumers.createInputMessageConsumer;
import static com.dungnguyen.kafkaflink.example.datapipeline.connector.Consumers.createStringConsumerForTopic;
import static com.dungnguyen.kafkaflink.example.datapipeline.connector.Producers.createBackupProducer;
import static com.dungnguyen.kafkaflink.example.datapipeline.connector.Producers.createStringProducer;

public class FlinkDataPipeline {

    public static void capitalize() throws Exception {
        String inputTopic = "flink_input";
        String outputTopic = "flink_output";
        String consumerGroup = "kafka-flink";
        String address = "localhost:9092";

        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();

        FlinkKafkaConsumer<String> flinkKafkaConsumer = createStringConsumerForTopic(inputTopic, address, consumerGroup);
        flinkKafkaConsumer.setStartFromEarliest();

        DataStream<String> stringInputStream = environment.addSource(flinkKafkaConsumer);

        FlinkKafkaProducer<String> flinkKafkaProducer = createStringProducer(outputTopic, address);

        stringInputStream.map(new WordsCapitalizer())
                .addSink(flinkKafkaProducer);

        environment.execute();
    }

    public static void createBackup() throws Exception {
        String inputTopic = "flink_input";
        String outputTopic = "flink_output";
        String consumerGroup = "kafka-flink";
        String kafkaAddress = "localhost:9092";

        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();

        environment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        FlinkKafkaConsumer<InputMessage> flinkKafkaConsumer = createInputMessageConsumer(inputTopic, kafkaAddress, consumerGroup);
        flinkKafkaConsumer.setStartFromEarliest();

        flinkKafkaConsumer.assignTimestampsAndWatermarks(new InputMessageTimestampAssigner());
        FlinkKafkaProducer<Backup> flinkKafkaProducer = createBackupProducer(outputTopic, kafkaAddress);

        DataStream<InputMessage> inputMessagesStream = environment.addSource(flinkKafkaConsumer);

        inputMessagesStream.timeWindowAll(Time.hours(24))
                .aggregate(new BackupAggregator())
                .addSink(flinkKafkaProducer);

        environment.execute();
    }

    public static void main(String[] args) throws Exception {
//        createBackup();
        capitalize();
    }

}