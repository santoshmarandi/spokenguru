package com.spokenenglishguru.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val category: String,
    @ColumnInfo(name = "pdf_pages")
    val pdfPages: String?,
    @ColumnInfo(name = "printed_pages")
    val printedPages: String?,
    val summary: String?,
    @ColumnInfo(name = "total_items")
    val totalItems: Int = 0
)
