
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id ("com.android.application") version "8.0.2" apply false
    id ("com.android.library") version "8.0.2" apply false
    id ("org.jetbrains.kotlin.android") version "1.8.20" apply false
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}
buildscript {
    // ...
    dependencies {
        // ...
        classpath("com.google.gms:google-services:4.4.0")  // Google 서비스 플러그인 버전은 최신 버전으로
    }

}
