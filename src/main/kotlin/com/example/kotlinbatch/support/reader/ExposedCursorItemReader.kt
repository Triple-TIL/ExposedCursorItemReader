package com.example.kotlinbatch.support.reader

import java.sql.ResultSet
import javax.sql.DataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transactionManager
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader

class ExposedCursorItemReader<T>(
    name: String,
    dataSource: DataSource,
    private var fetchSize: Int = 1000,
    private var clazz: Class<T>,
    private var query: () -> Query,
): AbstractItemCountingItemStreamItemReader<T>() {
    private var connection: Database

    private lateinit var resultSet: ResultSet
    private lateinit var transaction: Transaction


    init {
        setName(name)
        connection = Database.connect(dataSource)
        TransactionManager.defaultDatabase = connection
    }

    override fun doRead(): T? {
        if(resultSet.next()) {
            val instance = clazz.getDeclaredConstructor().newInstance()

            clazz.declaredFields.forEach {
                it.isAccessible = true
                it.set(instance, resultSet.getObject(it.name))
            }
            return instance
        }

        return null
    }

    override fun doOpen() {
        transaction = connection.transactionManager.newTransaction(readOnly = true)
        resultSet = query().fetchSize(fetchSize)
            .execute(transaction) ?: throw IllegalStateException("Query is null")
    }

    override fun doClose() {
        resultSet.close()
    }
}
