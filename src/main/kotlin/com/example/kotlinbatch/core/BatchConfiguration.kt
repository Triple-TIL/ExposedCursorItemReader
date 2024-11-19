package com.example.kotlinbatch.core

import com.example.kotlinbatch.domain.Users
import com.example.kotlinbatch.domain.UsersEntity
import com.example.kotlinbatch.support.reader.ExposedCursorItemReader
import javax.sql.DataSource
import org.jetbrains.exposed.sql.selectAll
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.support.ListItemReader
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class BatchConfiguration(
    private val transactionManager: PlatformTransactionManager,
    private val customItemWriter: CustomItemWriter
) {

    @Bean
    fun job(jobRepository: JobRepository,
            sampleStep: Step): Job {
        return JobBuilder("sampleJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(sampleStep)
            .build()
    }

    @Bean
    fun sampleStep(jobRepository: JobRepository,
                   @Qualifier("danalPayDataSource") danalPayDataSource: DataSource): Step {
        return StepBuilder("sampleStep", jobRepository)
            .chunk<Users, Users>(100, transactionManager)
            .reader(exposedCursorReader(danalPayDataSource))
            .writer(sampleWriter())
            .build()
    }

    @Bean
    fun getListReader(): ListItemReader<String> {
        return ListItemReader(listOf("foo", "bar", "baz"))
    }

    @Bean
    fun exposedCursorReader(danalPayDataSource: DataSource): ExposedCursorItemReader<Users> {
        return ExposedCursorItemReader(
            name = "exposedCursorReader",
            fetchSize = 1000 ,
            dataSource = danalPayDataSource,
            clazz = Users().javaClass
        ) {
            UsersEntity.selectAll().where { UsersEntity.id greaterEq 1 }
        }
    }

    @Bean
    fun sampleWriter(): CustomItemWriter {
       return customItemWriter
    }
}
