plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mook21"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mook21"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures{
        viewBinding = true
    }
    defaultConfig {
        vectorDrawables.useSupportLibrary = true
    }
}


dependencies {
    implementation(libs.gson)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.dotsindicator)
    implementation (libs.core)
    implementation (libs.material.v160)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)

    //glide bo tròn góc
    implementation ("jp.wasabeef:glide-transformations:4.3.0")

    // ExoPlayer cho việc phát media (chỉ giữ một phiên bản)
    implementation("com.google.android.exoplayer:exoplayer:2.18.1")
}