plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.room)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.slimdroid.lumix.core.database"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.androidx.core)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
}