plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.smartpantrymanager"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.smartpantrymanager"

        minSdk = 24
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // Android support libraries
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Material Design components
    implementation("com.google.android.material:material:1.12.0")

    // RecyclerView support
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // Unit testing
    testImplementation("junit:junit:4.13.2")

    // Android instrumentation testing
    androidTestImplementation(
        "androidx.test.ext:junit:1.1.5"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.5.1"
    )
}