package com.spokenenglishguru.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.spokenenglishguru.app.data.db.dao.LessonDao
import com.spokenenglishguru.app.data.db.dao.McqQuestionDao
import com.spokenenglishguru.app.data.db.dao.ProgressDao
import com.spokenenglishguru.app.data.db.dao.SentencePracticeDao
import com.spokenenglishguru.app.data.db.dao.VocabDao
import com.spokenenglishguru.app.data.db.entity.LessonEntity
import com.spokenenglishguru.app.data.db.entity.McqQuestionEntity
import com.spokenenglishguru.app.data.db.entity.SentencePracticeEntity
import com.spokenenglishguru.app.data.db.entity.UserProgressEntity
import com.spokenenglishguru.app.data.db.entity.VocabEntity

@Database(
    entities = [
        LessonEntity::class,
        SentencePracticeEntity::class,
        McqQuestionEntity::class,
        VocabEntity::class,
        UserProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun lessonDao(): LessonDao
    abstract fun sentencePracticeDao(): SentencePracticeDao
    abstract fun mcqQuestionDao(): McqQuestionDao
    abstract fun vocabDao(): VocabDao
    abstract fun progressDao(): ProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spoken_english_v2.db"
                )
                    .createFromAsset("database/spoken_english.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
