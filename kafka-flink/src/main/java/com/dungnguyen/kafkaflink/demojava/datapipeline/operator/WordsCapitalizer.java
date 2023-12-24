package com.dungnguyen.kafkaflink.demojava.datapipeline.operator;

import org.apache.flink.api.common.functions.MapFunction;

public class WordsCapitalizer implements MapFunction<String, String> {

    @Override
    public String map(String s) {
        return s.toUpperCase();
    }
}
