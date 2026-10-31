package com.spokenenglishguru.app

import android.app.Application
import com.spokenenglishguru.app.data.db.AppDatabase
import com.spokenenglishguru.app.data.repository.LessonRepository
import com.spokenenglishguru.app.data.repository.PracticeRepository
import com.spokenenglishguru.app.data.repository.QuizRepository
import com.spokenenglishguru.app.data.repository.VocabRepository

class SpokenEnglishApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    val lessonRepository by lazy {
        LessonRepository(database.lessonDao(), database.progressDao())
    }

    val practiceRepository by lazy {
        PracticeRepository(database.sentencePracticeDao())
    }

    val quizRepository by lazy {
        QuizRepository(database.mcqQuestionDao())
    }

    val vocabRepository by lazy {
        VocabRepository(database.vocabDao())
    }
}
