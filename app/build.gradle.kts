import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}

// local.properties 파일에서 API 키를 로드하는 부분
val properties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
}


android {

    namespace = "om.androidbook.medicine4"
    compileSdk = 34

    defaultConfig {
        applicationId = "om.androidbook.medicine4"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        multiDexEnabled = true
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Google API 키를 BuildConfig로 주입

    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }

    packaging {
        resources {
            pickFirsts.add("META-INF/DEPENDENCIES")
            pickFirsts.add("META-INF/INDEX.LIST")
            pickFirsts.add("META-INF/AL2.0")
            pickFirsts.add("META-INF/LGPL2.1")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.android.engage:engage-core:1.3.1")
    implementation("androidx.privacysandbox.tools:tools-core:1.0.0-alpha06")
    implementation("com.google.mlkit:vision-common:17.3.0")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(fileTree(mapOf("dir" to "libs", "includes" to listOf("*.aar", "*.jar"))))
    implementation(files("libs/libDaumMapAndroid.jar"))
    implementation("androidx.emoji2:emoji2-emojipicker:1.4.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.test:monitor:1.6.1")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    implementation("com.google.ar.sceneform:rendering:1.17.1")
    testImplementation("junit:junit:4.13.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.apis:google-api-services-vision:v1-rev451-1.25.0")
    implementation("com.google.api-client:google-api-client-android:2.2.0") // Google API 클라이언트
    implementation("com.google.api-client:google-api-client-gson:1.32.1") // JSON 처리를 위한 Gson 팩토리
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.google.cloud:google-cloud-vision:3.31.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.mlkit:text-recognition-korean:16.0.0")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition-korean:16.0.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.libraries.places:places:3.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.5.2")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.gms:google-services:4.4.0")



}