package com.dungnguyen.datapipelinekafkaflink.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

  @GetMapping("/{test}")
  String demoDevtool1(@PathVariable("test") String test) {
    return test;
  }

  @GetMapping("/devtool")
  String demoDevtool() {
    return "That is devtool demo abc 123 678";
  }
}
