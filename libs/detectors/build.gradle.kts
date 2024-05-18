plugins {
    id(libs.plugins.org.jetbrains.kotlin.android.get().pluginId)
    kotlin("kapt")
    alias(libs.plugins.com.google.dagger.hilt.android)
    id(libs.plugins.com.android.library.get().pluginId)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
}

android {
    namespace = "com.pawlowski.detectors"
    compileSdk = ProjectConfig.compileSdk

    defaultConfig {
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(platform(libs.org.jetbrains.kotlinx.kotlinx.serialization.bom))
    implementation(libs.bundles.serialization)
}
