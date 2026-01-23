package com.example.pvpeev.electronics_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class ElectronicsStoreApplication {

    static void main(String[] args) {
        SpringApplication.run(ElectronicsStoreApplication.class, args);
    }
}
