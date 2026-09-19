package com.cricpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CricPulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CricPulseApplication.class, args);
    }
}
