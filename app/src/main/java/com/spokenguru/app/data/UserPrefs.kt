package com.spokenguru.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Persists user learning stats, preferences, bookmarks, and daily streak using SharedPreferences
 * while exposing Compose-observable state for reactive UI updates.
 */
object UserPrefs {
    private const val PREF_NAME = "spoken_guru_user_prefs"
    private const val KEY_NAME = "learner_name"
    private const val KEY_DAILY_GOAL = "daily_goal"
    private const val KEY_TODAY_COUNT = "today_count"
    private const val KEY_LAST_DATE = "last_practice_date"
    private const val KEY_STREAK = "streak_days"
    private const val KEY_XP = "total_xp"
    private const val KEY_LAST_LESSON = "last_opened_lesson"
    private const val KEY_AUTO_AUDIO = "auto_play_audio"
    private const val KEY_BOOKMARKS = "bookmarked_lessons"
    private const val KEY_SMART_DISTRACTORS = "smart_distractors_enabled"

    var learnerName by mutableStateOf("Spoken Learner")
        private set

    var dailyGoal by mutableIntStateOf(20)
        private set

    var todayPracticed by mutableIntStateOf(0)
        private set

    var streakDays by mutableIntStateOf(0)
        private set

    var totalXp by mutableIntStateOf(0)
        private set

    var lastOpenedLesson by mutableIntStateOf(1)
        private set

    var autoPlayAudio by mutableStateOf(true)
        private set

    var smartDistractorsEnabled by mutableStateOf(true)
        private set

    var bookmarkedLessons by mutableStateOf<Set<Int>>(emptySet())
        private set

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private fun todayString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)

    private fun yesterdayString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    fun init(context: Context) {
        val p = prefs(context)
        learnerName = p.getString(KEY_NAME, "Spoken Learner") ?: "Spoken Learner"
        dailyGoal = p.getInt(KEY_DAILY_GOAL, 20)
        totalXp = p.getInt(KEY_XP, 0)
        lastOpenedLesson = p.getInt(KEY_LAST_LESSON, 1)
        autoPlayAudio = p.getBoolean(KEY_AUTO_AUDIO, true)
        smartDistractorsEnabled = p.getBoolean(KEY_SMART_DISTRACTORS, true)

        val savedBookmarks = p.getStringSet(KEY_BOOKMARKS, emptySet()) ?: emptySet()
        bookmarkedLessons = savedBookmarks.mapNotNull { it.toIntOrNull() }.toSet()

        val lastDate = p.getString(KEY_LAST_DATE, "") ?: ""
        val today = todayString()
        val yesterday = yesterdayString()

        todayPracticed = if (lastDate == today) p.getInt(KEY_TODAY_COUNT, 0) else 0
        streakDays = when (lastDate) {
            today, yesterday -> p.getInt(KEY_STREAK, 0)
            else -> 0
        }
    }

    fun setLastOpenedLesson(context: Context, lessonNumber: Int) {
        if (lessonNumber <= 0) return
        lastOpenedLesson = lessonNumber
        prefs(context).edit().putInt(KEY_LAST_LESSON, lessonNumber).apply()
    }

    fun recordAnswerCompleted(context: Context, lessonNumber: Int, xpReward: Int = 10) {
        val p = prefs(context)
        val today = todayString()
        val yesterday = yesterdayString()
        val lastDate = p.getString(KEY_LAST_DATE, "") ?: ""

        val newStreak = when (lastDate) {
            today -> maxOf(1, p.getInt(KEY_STREAK, 1))
            yesterday -> p.getInt(KEY_STREAK, 0) + 1
            else -> 1
        }
        val newTodayCount = (if (lastDate == today) p.getInt(KEY_TODAY_COUNT, 0) else 0) + 1
        val newXp = p.getInt(KEY_XP, 0) + xpReward

        streakDays = newStreak
        todayPracticed = newTodayCount
        totalXp = newXp
        lastOpenedLesson = lessonNumber

        p.edit()
            .putString(KEY_LAST_DATE, today)
            .putInt(KEY_STREAK, newStreak)
            .putInt(KEY_TODAY_COUNT, newTodayCount)
            .putInt(KEY_XP, newXp)
            .putInt(KEY_LAST_LESSON, lessonNumber)
            .apply()
    }

    fun updateLearnerName(context: Context, name: String) {
        val cleaned = name.trim().ifBlank { "Spoken Learner" }
        learnerName = cleaned
        prefs(context).edit().putString(KEY_NAME, cleaned).apply()
    }

    fun updateDailyGoal(context: Context, goal: Int) {
        dailyGoal = goal.coerceIn(5, 200)
        prefs(context).edit().putInt(KEY_DAILY_GOAL, dailyGoal).apply()
    }

    fun updateAutoPlayAudio(context: Context, enabled: Boolean) {
        autoPlayAudio = enabled
        prefs(context).edit().putBoolean(KEY_AUTO_AUDIO, enabled).apply()
    }

    fun updateSmartDistractors(context: Context, enabled: Boolean) {
        smartDistractorsEnabled = enabled
        prefs(context).edit().putBoolean(KEY_SMART_DISTRACTORS, enabled).apply()
    }

    fun toggleBookmark(context: Context, lessonNumber: Int) {
        val updated = if (bookmarkedLessons.contains(lessonNumber)) {
            bookmarkedLessons - lessonNumber
        } else {
            bookmarkedLessons + lessonNumber
        }
        bookmarkedLessons = updated
        prefs(context).edit()
            .putStringSet(KEY_BOOKMARKS, updated.map { it.toString() }.toSet())
            .apply()
    }

    fun getInitials(name: String = learnerName): String {
        val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        return when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "SG"
        }
    }

    fun getLevelTitle(xp: Int = totalXp): Pair<Int, String> {
        return when {
            xp < 100 -> 1 to "Beginner"
            xp < 350 -> 2 to "Explorer"
            xp < 750 -> 3 to "Speaker"
            xp < 1500 -> 4 to "Conversational"
            xp < 3000 -> 5 to "Fluent"
            else -> (5 + xp / 1500) to "English Guru"
        }
    }
}
