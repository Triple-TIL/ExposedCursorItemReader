package com.example.kotlinbatch.global.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import org.jetbrains.exposed.spring.SpringTransactionManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
class OneApiDataSourceConfig {

    @Bean
    @Qualifier("oneApiHikariConfig")
    @ConfigurationProperties(prefix = "spring.datasource.hikari.oneapi")
    fun oneApiHikariConfig(): HikariConfig {
        return HikariConfig()
    }

    @Bean
    @Primary
    @Qualifier("oneApiDataSource")
    fun oneApiDataSource(): DataSource {
        return HikariDataSource(oneApiHikariConfig())
    }

    @Bean
    @Primary
    fun oneApiTransactionManager(): PlatformTransactionManager {
        return SpringTransactionManager(oneApiDataSource())
    }

    @Bean
    @Primary
    @ConditionalOnProperty("spring.exposed.generate-ddl", havingValue = "true")
    fun oneApiExposedDatabaseInitializer(): ExposedDatabaseInitializer {
        return ExposedDatabaseInitializer(oneApiTransactionManager(), "com.example.entity.oneapi")
    }

}

