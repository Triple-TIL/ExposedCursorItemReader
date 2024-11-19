package com.example.kotlinbatch.core

import com.example.kotlinbatch.domain.Users
import com.example.kotlinbatch.domain.UsersEntity
import org.jetbrains.exposed.sql.insert
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional("oneApiTransactionManager")
class CustomItemWriter: ItemWriter<Users> {

    override fun write(chunk: Chunk<out Users>) {
        for (item in chunk.items) {
            UsersEntity.insert { entity ->
                entity[name] = item.name
                entity[age] = item.age
            }
        }
    }
}
