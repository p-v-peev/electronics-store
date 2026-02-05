package com.example.pvpeev.electronics_store.repository;

import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;

@DataJdbcTest
public class BaseRepositoryTest {
    public static final String POSTGRES_VERSION = "postgres:18.1";
}
