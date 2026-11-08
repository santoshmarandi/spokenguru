# Spoken Guru — Duolingo-style English Learning Android App

An interactive Android application built with **Kotlin** and **Jetpack Compose (Material 3)** inspired by Duolingo's word-pick mechanics. The curriculum is based on the **Spoken English Guru** practice book.

---

## 🌟 Key Features

1. **Duolingo-style Word-Pick Quiz**:
   - **Sentence Prompt**: Displays Hindi sentence in bold with Roman transliteration.
   - **Interactive Audio**:
     - 🔊 **Normal Speed Audio (1.0x)**: Clean English TTS pronunciation.
     - 🐢 **Turtle Slow Speed (0.6x)**: Slow and deliberate enunciation for beginners.
     - 💡 **Vocabulary Hints**: Instant contextual hints for difficult words.
   - **Word Bank & Slot**:
     - Shuffled word tiles with 2–4 smart distractors.
     - Tap word tiles to place them into the sentence slot; tap to remove.
     - Visual ghost placeholders in the bank for active words.
   - **Instant Feedback**:
     - Green celebration banner with audio chime on correct answer.
     - Red corrective banner showing the exact solution on error.
     - Minimalist mode: No punishing hearts or life limits.
   - **Flexible Checking**:
     - Strips punctuation (`.`, `?`, `!`, `,`) automatically.
     - Accepts valid alternative translations (e.g., Sentence 91 accepts both *"This isn't a good time"* and *"This time is not good"*).

2. **Full Curriculum (Lessons 1 to 40)**:
   - **All 40 Lessons** are fully loaded with **8,207 interactive practice questions** extracted and enriched from the complete Spoken English Guru practice book.
   - Tailored multi-format interactive curriculum:
     - 🧱 **`SENTENCE_BUILDER`** (6,526 full sentences): 31 translation chapters (Tenses, Modals, Conditionals, Prepositions, Conjunctions, Voice, Narration, Test Papers, etc.).
     - 🔄 **`WORD_MATCH`** (251 match rounds / 1,000+ pairs): 6 vocabulary chapters (Verbs V1-V2, Adjective & Adverb degrees, Antonyms, Synonyms, Singular-to-Plural).
     - ❓ **`MCQ`** (1,204 questions): 1 chapter (Lesson 39: 1500+ MCQs).
     - ⚠️ **`SENTENCE_CORRECTION`** (196 questions): 1 chapter (Lesson 37: Error Spotting).
     - 📝 **`FILL_BLANK`** (30 questions): 1 chapter (Lesson 31: Homonyms).

3. **Smart Guru Error Feedback & Mistake Analyzer**:
   - Universal Error Feedback card on incorrect answers showing "आपका उत्तर", "क्या गलती हुई" (Smart mistake reasoning in Hindi), "सही उत्तर", and comprehensive Hindi grammar rules.
   - Dedicated **"Revise Mistakes"** notebook screen to replay and master previously missed questions.

4. **Sleek Question Navigation**:
   - Top Bar **`←` (Prev)** and **`→` (Next)** arrow buttons beside the question progress bar for effortless jumping forward and backward.
   - Full-width, clean **Check Answer** primary action button in the bottom bar.

4. **Room SQLite Local Database & In-App Importer**:
   - Bundled with `lessons_seed.json` in assets for offline launch.
   - Includes an in-app **Manage & Import** screen allowing users to easily paste JSON data for Lessons 2–40 without reinstalling the app.

5. **Dynamic Theming & Offline Sounds**:
   - Vibrant Duolingo Green (`#58CC02`) Light Theme + OLED Dark Mode.
   - 100% offline audio chime synthesis using Android's native PCM `AudioTrack`.

---

## 🏗️ Project Architecture

```
app/src/main/
├── assets/
│   └── lessons_seed.json             # Seed data with all 40 lessons & 101 Lesson 1 sentences
├── java/com/spokenguru/app/
│   ├── MainActivity.kt               # Navigation Host & Entry point
│   ├── audio/
│   │   ├── SoundEffectManager.kt     # Native PCM audio chimes (success, error, tile click)
│   │   └── TtsManager.kt             # Android Text-To-Speech with dual speed (1.0x & 0.6x)
│   ├── data/
│   │   ├── AppDatabase.kt            # Room Database
│   │   ├── model/
│   │   │   ├── LessonEntity.kt       # Lesson table definition
│   │   │   └── QuestionEntity.kt     # Question table definition
│   │   ├── dao/
│   │   │   ├── LessonDao.kt          # Lesson queries & progress updates
│   │   │   └── QuestionDao.kt        # Question queries & mistake flags
│   │   └── repository/
│   │       └── LessonRepository.kt   # Asset seeder & JSON importer
│   └── ui/
│       ├── components/
│       │   ├── DuoButton.kt          # Duolingo 3D pressed-state button
│       │   └── WordChip.kt           # Word tile chips & placeholders
│       ├── screens/
│       │   ├── LessonListScreen.kt   # All 40 lessons list & progress tracker
│       │   ├── WordPickScreen.kt     # Core Duolingo-style word-pick quiz
│       │   ├── MistakeReviewScreen.kt# Targeted mistake revision mode
│       │   └── ImportLessonScreen.kt # In-app JSON importer & database manager
│       └── theme/
│           ├── Color.kt              # Duolingo color palette
│           ├── Theme.kt              # Dynamic Light & Dark Material3 themes
│           └── Type.kt               # Typography styles
```

---

## 🚀 How to Build and Run

1. **Build Debug APK**:
   ```powershell
   .\gradlew.bat assembleDebug
   ```
2. **Output APK Location**:
   `app/build/outputs/apk/debug/app-debug.apk`

3. **Install to Connected Android Device**:
   ```powershell
   adb install app\build\outputs\apk\debug\app-debug.apk
   ```
