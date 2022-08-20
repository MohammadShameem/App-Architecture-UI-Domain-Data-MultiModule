
import dependencies.addDateFormatterModule
import dependencies.addRoomCommonDependencies

plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    kotlin("kapt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    addRoomCommonDependencies()
    addDateFormatterModule()
}