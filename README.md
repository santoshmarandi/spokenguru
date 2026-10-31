# Spoken English Guru: English Bolna Sikhein
### Full-Fledged Modern Android Application (Built from Scratch)

---

## 📱 Overview

**Spoken English Guru** is a comprehensive, offline-first Android learning application built using **Modern Native Android (Kotlin, Jetpack Compose, Material 3, Room SQLite)**. It transforms the extracted eBook curriculum (`Spoken_English_Guru_extraction_complete.zip`) into an interactive, gamified ed-tech app.

---

## 📊 Extracted Curriculum & Database Statistics

- **40 Complete Lessons** (Demonstratives, Tenses, Modals, Passive Voice, Conditionals, Prepositions, Causative Verbs, Conjunctions, Direct/Indirect Speech, etc.)
- **5,580 Bilingual Practice Sentences** (Hindi Prompt + Romanized Hint + English Answer)
- **1,234 Multiple Choice Questions (MCQs)** spread across 67 quizzes with verified answer keys
- **746 Vocabulary Items** (3-Forms of Verbs V1/V2/V3, Antonyms, Synonyms, Adjective/Adverb Degrees)
- **100% Offline-First**: Pre-populated SQLite Room Database (`spoken_english.db`) packaged into app assets for zero latency and no internet requirement.

---

## 🌟 Key Features & 4-Pillar Architecture

### Pillar 1: Concept Lessons & Notes
- Categorized browser for all 40 lessons (Foundation, Grammar Mechanics, Vocabulary, Applied Practice, Quizzes).
- Real-time search across all lessons.
- Lesson detail view featuring full bilingual sentence pairs with bookmarking and individual pronunciation.

### Pillar 2: Sentence Translation Lab
- **Smart Flashcards**: Flip Hindi sentences to reveal English answers with smooth transitions, TTS pronunciation audio, and "Mastered" vs "Need Practice" tracking.
- **Word Jumble / Sentence Puzzle**: Interactive game where words are scattered and users tap word chips to construct the grammatically correct sentence. Immediate audio/visual validation.
- **Starred Favorites**: Practice only sentences you've bookmarked during your lessons.

### Pillar 3: 1500+ MCQ Quizzes & Test Series
- **67 Topic Quizzes**: Instant feedback on answer selection (Green for correct, Red for wrong, automatic explanation highlight).
- **Mistake Bank**: Any question answered incorrectly is automatically captured in a dedicated "Mistake Bank" so users can re-test their weak areas.
- **Quiz Scorecards**: Comprehensive completion statistics, percentages, and performance evaluation.

### Pillar 4: Vocab Vault
- Searchable directory of Verbs (V1, V2, V3), Antonyms (Opposites), Synonyms, and Degrees of Comparison.
- Instant search by English word or Hindi meaning.
- Text-to-Speech pronunciation button for every vocabulary item.

---

## 🛠️ Technology Stack

- **UI**: 100% Declarative UI with Jetpack Compose & Material 3
- **Language**: Kotlin 1.9.24 / JVM 17
- **Database**: Android Jetpack Room 2.6.1 (Pre-populated SQLite)
- **Architecture**: MVVM + Repository Pattern + Clean Architecture
- **Speech Engine**: Native Android `TextToSpeech` API (English US/UK)
- **Navigation**: Jetpack Compose Navigation (`NavHost`)
- **Build System**: Gradle 8.9 + Android Gradle Plugin 8.4.2 (Compile SDK 34, Min SDK 26)

---

## 🚀 How to Install & Run

### 1. Direct APK Installation
The debug APK is compiled and ready in the project directory:
- [SpokenEnglishGuru.apk](file:///home/ubuntu/Downloads/New%20Folder/SpokenEnglishGuru.apk) (16 MB)
- Transfer `SpokenEnglishGuru.apk` to any Android phone (Android 8.0+) and install.
- Or install via ADB:
  ```bash
  adb install SpokenEnglishGuru.apk
  ```

### 2. Opening in Android Studio
1. Open Android Studio.
2. Select **Open** and select the folder:
   `/home/ubuntu/Downloads/New Folder/SpokenEnglishGuruApp`
3. Let Gradle sync and press **Run** (`Shift + F10`) to launch on emulator or connected phone.

---

## 📁 Project Structure

```
SpokenEnglishGuruApp/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── assets/database/spoken_english.db
│       └── java/com/spokenenglishguru/app/
│           ├── MainActivity.kt
│           ├── SpokenEnglishApp.kt
│           ├── data/
│           │   ├── db/
│           │   │   ├── AppDatabase.kt
│           │   │   ├── dao/
│           │   │   │   ├── LessonDao.kt
│           │   │   │   ├── SentencePracticeDao.kt
│           │   │   │   ├── McqQuestionDao.kt
│           │   │   │   ├── VocabDao.kt
│           │   │   │   └── ProgressDao.kt
│           │   │   └── entity/
│           │   └── repository/
│           ├── ui/
│           │   ├── components/ (BottomNavBar, AppTopBar)
│           │   ├── navigation/ (Screen routes)
│           │   ├── theme/ (Color, Theme, Type)
│           │   └── screens/
│           │       ├── home/ (HomeScreen, HomeViewModel)
│           │       ├── lessons/ (LessonsScreen, LessonDetailScreen, LessonsViewModel)
│           │       ├── practice/ (PracticeScreen, FlashcardPracticeScreen, WordJumblePracticeScreen, PracticeViewModel)
│           │       ├── quiz/ (QuizzesScreen, QuizPlayScreen, MistakeBankScreen, QuizViewModel)
│           │       └── vocab/ (VocabScreen, VocabViewModel)
│           └── util/ (TextToSpeechManager.kt)
```
