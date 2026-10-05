plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.ecommerce.ecom"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.ecommerce.ecom"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    lint {
        abortOnError       = true          // keep this true for CI
        checkReleaseBuilds = true
        baseline           = file("lint-baseline.xml")
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    //Retrofit
    implementation ("com.google.code.gson:gson:2.9.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")

    // Room components
    implementation ("androidx.room:room-ktx:2.7.2")
    implementation ("androidx.room:room-runtime:2.7.2")
    ksp("androidx.room:room-compiler:2.7.2")

    implementation ("androidx.activity:activity-compose:1.5.0")
    implementation ("androidx.fragment:fragment-ktx:1.6.1")
    androidTestImplementation ("androidx.room:room-testing:2.7.2")

    // Lifecycle components
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-common-java8:2.6.1")

    // Kotlin components
    api ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    api ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    // SwipeLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}