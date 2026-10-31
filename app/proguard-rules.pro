# Proguard rules for SpokenEnglishGuruApp
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}
