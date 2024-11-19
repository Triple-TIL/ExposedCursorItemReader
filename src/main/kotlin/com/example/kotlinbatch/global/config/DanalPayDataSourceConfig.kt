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
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
class DanalPayDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari.danalpay")
    fun danalPayHikariConfig(): HikariConfig {
        return HikariConfig()
    }

    @Bean
    @Qualifier("danalPayDataSource")
    fun danalPayDataSource(): DataSource {
        return HikariDataSource(danalPayHikariConfig())
    }

    @Bean
    @Qualifier("danalPayTransactionManager")
    fun danalPayTransactionManager(): PlatformTransactionManager {
        return SpringTransactionManager(danalPayDataSource())
    }

    @Bean
    @ConditionalOnProperty("spring.exposed.generate-ddl", havingValue = "true")
    fun primaryExposedDatabaseInitializer(): ExposedDatabaseInitializer {
        return ExposedDatabaseInitializer(danalPayTransactionManager(), "com.example.entity.primary")
    }



}
