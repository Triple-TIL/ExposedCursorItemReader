package com.example.kotlinbatch.core

import com.example.kotlinbatch.domain.Users
import com.example.kotlinbatch.domain.UsersEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.statements.BatchUpdateStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional("oneApiTransactionManager")
class CustomItemWriter: ItemWriter<Users> {

    override fun write(chunk: Chunk<out Users>) {

        BatchInsertStatement(UsersEntity).apply {
            chunk.chunked(1_000).forEach { users ->
                addBatch()
                users.forEach {
                    this[UsersEntity.id] = EntityID(it.id, UsersEntity)
                    this[UsersEntity.name] = it.name
                    this[UsersEntity.age] = it.age
                }
            }
        }.execute(TransactionManager.current())
    }
}
