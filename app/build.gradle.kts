plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize") // <--- Add this line
}

android {
    namespace = "com.example.ngo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ngo"
        minSdk = 24
        targetSdk = 36
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)



    implementation("androidx.core:core-splashscreen:1.0.0")

    implementation(platform("com.google.firebase:firebase-bom:34.10.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("io.appwrite:sdk-for-android:12.0.0")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database")
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation("com.razorpay:checkout:1.6.41")

}