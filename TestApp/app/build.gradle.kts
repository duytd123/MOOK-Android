plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.testapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.testapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Core AndroidX libraries
    implementation(libs.core)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.lifecycle.viewmodel.android)

    // Networking
    implementation(libs.volley)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // Image loading
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // Room database
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Media player
    implementation(libs.exoplayer)

    // gRPC and Protobuf
    implementation(libs.grpc.okhttp)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.kotlin.stub)
    implementation(libs.javax.annotation.api)
    implementation(libs.protobuf.java)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.multidex)
    implementation(libs.lifecycle.runtime.ktx)
}
