// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.22" apply false
    id("com.android.application") version "8.1.2" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
}

buildscript {
    repositories {
        google()
    }
}