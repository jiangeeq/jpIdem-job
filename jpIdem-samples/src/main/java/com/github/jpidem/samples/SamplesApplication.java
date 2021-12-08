package com.github.jpidem.samples;

import com.github.jpidem.spring4.EnableRetrying;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRetrying(proxyTargetClass = true)
@SpringBootApplication
public class SamplesApplication {
    public static void main(String[] args) {
        SpringApplication.run(SamplesApplication.class, args);
    }
}