package com.spokenenglishguru.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mcq_questions")
data class McqQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "lesson_id")
    val lessonId: Int,
    @ColumnInfo(name = "quiz_num")
    val quizNum: Int,
    @ColumnInfo(name = "question_num")
    val questionNum: Int,
    val prompt: String,
    @ColumnInfo(name = "option_a")
    val optionA: String,
    @ColumnInfo(name = "option_b")
    val optionB: String,
    @ColumnInfo(name = "option_c")
    val optionC: String?,
    @ColumnInfo(name = "option_d")
    val optionD: String?,
    @ColumnInfo(name = "correct_option")
    val correctOption: String, // 'a', 'b', 'c', 'd'
    val explanation: String? = null,
    @ColumnInfo(name = "is_bookmarked")
    val isBookmarked: Int = 0,
    @ColumnInfo(name = "times_wrong")
    val timesWrong: Int = 0
)
