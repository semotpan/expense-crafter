package com.semotpan.expensecrafter

import com.semotpan.expensecrafter.shared.ApiExceptionHandler
import org.springframework.boot.SpringApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
@Import(ApiExceptionHandler)
class TestServerApplication {

    @Bean
    @ServiceConnection
    MySQLContainer<? extends MySQLContainer> mysqlContainer() {
        return new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
    }

    static void main(String[] args) {
        SpringApplication.from(ServerApplication::main)
                .with(TestServerApplication.class)
                .run(args)
    }
}
