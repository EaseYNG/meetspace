package com.venus.meetspace;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.venus.meetspace.repository")
public class MeetspaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetspaceApplication.class, args);
    }
}
