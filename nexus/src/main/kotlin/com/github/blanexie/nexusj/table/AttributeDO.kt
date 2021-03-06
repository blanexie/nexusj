package com.github.blanexie.nexusj.table

import com.github.blanexie.nexusj.support.database
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.Entity
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.singleOrNull
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text
import org.ktorm.schema.varchar

/*********************** 配置信息表 ******************************/

val Database.attributeDO get() = this.sequenceOf(Attribute)

interface AttributeDO : Entity<AttributeDO> {
    companion object : Entity.Factory<UserDO>() {
        fun findById(id: Int): AttributeDO? {
            return database().attributeDO.singleOrNull { it.id eq id }
        }
        fun findByTypeAndName(type: String, name:String): AttributeDO? {
            return database().attributeDO.singleOrNull { (it.type eq  type) and (it.name eq name)}
        }
    }

    var id: Int

    //配置类型， 'label':"标签"
    var type: String
    var name: String
    var value: String

    fun save() {
        database().attributeDO.add(this)
    }
}

object Attribute : Table<AttributeDO>("role") {
    var id = int("id").primaryKey().bindTo { it.id }
    var type = varchar("type").bindTo { it.type }
    var name = varchar("name").bindTo { it.name }
    var value = text("value").bindTo { it.value }
}