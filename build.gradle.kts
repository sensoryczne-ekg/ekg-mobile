plugins {
    alias(libs.plugins.com.google.dagger.hilt.android) apply false
}

buildscript {
    dependencies {
        classpath(libs.com.android.tools.build.gradle)
        classpath(libs.org.jetbrains.kotlin.gradle.plugin)
        classpath(libs.com.google.protobuf.gradle.plugin)
    }
}
