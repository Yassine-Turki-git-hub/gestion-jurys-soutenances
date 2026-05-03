package com.soutenance.soutenance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SoutenanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoutenanceApplication.class, args);
    }
}
