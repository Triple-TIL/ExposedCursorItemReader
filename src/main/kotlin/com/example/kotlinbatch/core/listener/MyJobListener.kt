package com.example.kotlinbatch.core.listener

import com.example.kotlinbatch.domain.UsersEntity
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MyJobListener: JobExecutionListener {

//    @Transactional("danalPayTransactionManager")
    override fun beforeJob(jobExecution: JobExecution) {
        super.beforeJob(jobExecution)

        transaction {
            SchemaUtils.create(UsersEntity)
            for(i in 1..30)  {
                UsersEntity.insert {
                    it[name] = "Alice"
                    it[age] = i
                }
            }
        }
    }

    override fun afterJob(jobExecution: JobExecution) {
        super.afterJob(jobExecution)
    }
}
