package com.videogenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VideoGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoGeneratorApplication.class, args);
    }
}
