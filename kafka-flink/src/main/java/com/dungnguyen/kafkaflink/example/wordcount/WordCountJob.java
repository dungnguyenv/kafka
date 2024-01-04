package com.dungnguyen.kafkaflink.example.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.Properties;

public class WordCountJob {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties sourceProperties = new Properties();
        sourceProperties.setProperty("bootstrap.servers", "localhost:9092");
        sourceProperties.setProperty("group.id", "flink-group");

        Properties sinkProperties = new Properties();
        sinkProperties.setProperty("bootstrap.servers", "localhost:9092");

        // Kafka source
        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>("input-topic", new SimpleStringSchema(), sourceProperties);
        DataStream<String> inputSentenceStream = env.addSource(kafkaConsumer);

        // Word count logic
        DataStream<Tuple2<String, Integer>> wordCounts = inputSentenceStream
                .flatMap(new WordCountFlatMap())
                .keyBy(0)
                .sum(1);

        wordCounts.print();

        // Kafka sink
        FlinkKafkaProducer<String> kafkaProducer = new FlinkKafkaProducer<>("output-topic", new SimpleStringSchema(), sinkProperties);
        wordCounts.map(tuple -> tuple.toString()).addSink(kafkaProducer);

        env.execute("WordCountJob");
    }

    public static class WordCountFlatMap implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) {

      System.out.println("Sentence: "+  sentence);
            // Split the sentence into words
            String[] words = sentence.toLowerCase().split("\\W+");

            // Emit the count for each word
            for (String word : words) {
                if (word.length() > 0) {
                    out.collect(new Tuple2<>(word, 1));
                }
            }

      System.out.println("Output: " + out);
    }
    }
}
