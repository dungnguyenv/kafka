package com.dungnguyen.datapipelinekafkaflink.kafkastream.countingmessages;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WordCountProcessor {

  private static final Serde<String> STRING_SERDE = Serdes.String();

  @Autowired
  void buildPipeline(StreamsBuilder streamsBuilder) {

    KStream<String, String> messageStream =
        streamsBuilder.stream(
            "count-message-input-topic", Consumed.with(STRING_SERDE, STRING_SERDE));

    KTable<String, Long> wordCounts =
        messageStream
            .mapValues((ValueMapper<String, String>) String::toLowerCase)
            .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
            .groupBy((key, word) -> word, Grouped.with(STRING_SERDE, STRING_SERDE))
            .count(Materialized.as("CountsStore"));

//    wordCounts.toStream().to("count-message-output-topic", Produced.with(Serdes.String(), Serdes.Long()));

    KTable<String, String> stringValue = wordCounts.mapValues(String::valueOf);

    stringValue.toStream().to("count-message-output-topic");
  }
}


