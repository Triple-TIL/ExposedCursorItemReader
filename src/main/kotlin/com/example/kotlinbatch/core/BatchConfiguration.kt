package com.example.kotlinbatch.core

import com.example.kotlinbatch.core.job.ExposedCursorItemReader
import com.example.kotlinbatch.core.listener.MyJobListener
import com.example.kotlinbatch.domain.Users
import com.example.kotlinbatch.domain.UsersEntity
import org.jetbrains.exposed.sql.selectAll
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.support.ListItemReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class BatchConfiguration(
    private val dataSource: DataSource,
    private val transactionManager: PlatformTransactionManager
) {

    @Bean
    fun job(jobRepository: JobRepository,
            sampleStep: Step): Job {
        return JobBuilder("sampleJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .listener(MyJobListener())
            .start(sampleStep)
            .build()
    }

    @Bean
    fun sampleStep(jobRepository: JobRepository): Step {
        return StepBuilder("sampleStep", jobRepository)
            .chunk<Users, Users>(100, transactionManager)
            .reader(exposedCursorReader())
            .writer(sampleWriter())
            .build()
    }

    @Bean
    fun getListReader(): ListItemReader<String> {
        return ListItemReader(listOf("foo", "bar", "baz"))
    }

    @Bean
    fun exposedCursorReader(): ExposedCursorItemReader<Users> {
        return ExposedCursorItemReader(
            name = "exposedCursorReader",
            fetchSize = 1000 ,
            dataSource = dataSource,
            clazz = Users().javaClass
        ) {
            UsersEntity.selectAll().where { UsersEntity.id greaterEq 1 }
        }
    }

    @Bean
    fun sampleWriter(): ItemWriter<Users> {
        return ItemWriter<Users> { items ->
            items.forEach { println("writing $it") }
        }
    }

}
