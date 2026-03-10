plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.slimdroid.lumix.core.data"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 26
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
}

dependencies {
    api(projects.core.model)
    implementation(projects.core.database)

    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
}