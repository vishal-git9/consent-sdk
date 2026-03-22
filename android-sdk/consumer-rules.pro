# Consent SDK ProGuard rules
-keep class com.niceoneconsent.sdk.** { *; }
-keepclassmembers class com.niceoneconsent.sdk.models.** { *; }
-dontwarn io.ktor.**
-dontwarn kotlinx.serialization.**
