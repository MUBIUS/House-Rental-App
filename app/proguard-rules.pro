# ============================================================
# ProGuard rules for House Rental App
# ============================================================

# Preserve stack traces for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep generic signatures for type-safe deserialization
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# ── Firebase Realtime Database ────────────────────────────
# Keep all model classes used for Firebase serialization
-keep class com.example.houserentalapp.model.** { *; }

# Firebase internal classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ── Firebase Auth ─────────────────────────────────────────
-keep class com.google.firebase.auth.** { *; }

# ── Glide ─────────────────────────────────────────────────
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-dontwarn com.bumptech.glide.**

# ── Lottie ────────────────────────────────────────────────
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }

# ── MPAndroidChart ────────────────────────────────────────
-keep class com.github.mikephil.charting.** { *; }

# ── PhotoView ─────────────────────────────────────────────
-keep class com.github.chrisbanes.photoview.** { *; }

# ── Shimmer ───────────────────────────────────────────────
-keep class com.facebook.shimmer.** { *; }

# ── Jetpack Security (EncryptedSharedPreferences) ─────────
-keep class androidx.security.crypto.** { *; }

# ── Material Components ───────────────────────────────────
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# ── General AndroidX ──────────────────────────────────────
-keep class androidx.** { *; }
-dontwarn androidx.**

# ── Suppress common warnings ──────────────────────────────
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**