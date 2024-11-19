package com.example.kotlinbatch.global.config

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AssignableTypeFilter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition

class ExposedDatabaseInitializer (
    private val platformTransactionManager: PlatformTransactionManager,
    private val basePackage: String,
) : ApplicationRunner {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(args: ApplicationArguments) {
        val tables = findTables()

        if (tables.isNotEmpty()) {
            transaction {
                SchemaUtils.drop(*tables.toTypedArray())
                SchemaUtils.create(*tables.toTypedArray())
            }
        }
    }

    private fun findTables(): List<Table> {
        val provider = ClassPathScanningCandidateComponentProvider(false)
        provider.addIncludeFilter(AssignableTypeFilter(Table::class.java))
        val components = provider.findCandidateComponents(basePackage)
        return components.map { Class.forName(it.beanClassName).kotlin.objectInstance as Table }
    }

    private fun transaction(block: () -> Unit) {
        val status = platformTransactionManager.getTransaction(DefaultTransactionDefinition())
        try {
            block()
            platformTransactionManager.commit(status)
        } catch (e: Exception) {
            platformTransactionManager.rollback(status)
            throw e
        }
    }
}
