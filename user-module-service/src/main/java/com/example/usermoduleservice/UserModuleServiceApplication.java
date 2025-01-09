package com.example.usermoduleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserModuleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserModuleServiceApplication.class, args);
    }

}
