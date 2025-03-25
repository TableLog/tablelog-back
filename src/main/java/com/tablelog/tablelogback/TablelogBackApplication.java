package com.tablelog.tablelogback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class TablelogBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(TablelogBackApplication.class, args);
    }

}
