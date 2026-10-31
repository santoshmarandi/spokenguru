package com.spokenenglishguru.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey
    val key: String,
    @ColumnInfo(name = "value_int")
    val valueInt: Int = 0,
    @ColumnInfo(name = "value_text")
    val valueText: String? = null
)
