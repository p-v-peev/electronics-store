package com.example.pvpeev.electronics_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
@EnableJdbcRepositories
public class ElectronicsStoreApplication {

    static void main(String[] args) {
        SpringApplication.run(ElectronicsStoreApplication.class, args);
    }
}
