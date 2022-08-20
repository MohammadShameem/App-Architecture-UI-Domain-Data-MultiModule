buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven ("https://jitpack.io")
        maven ("https://oss.jfrog.org/libs-snapshot")
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:${AppConfig.buildGradle_version}")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:${AppConfig.hilt_android_version}")
       // classpath ("com.google.gms:google-services:${AppConfig.googleServicesVersion}")
       // classpath ("com.google.firebase:firebase-crashlytics-gradle:${AppConfig.firebaseCrashAnalyticGradleVersion}")
    }
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven ("https://jitpack.io")
        maven ("https://oss.jfrog.org/libs-snapshot")
    }
}

tasks.create<Delete>("clean") {
    delete  = setOf(
        rootProject.buildDir
    )
}
