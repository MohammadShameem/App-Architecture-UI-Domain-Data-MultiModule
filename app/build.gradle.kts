import dependencies.*

plugins {
    id ("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id ("dagger.hilt.android.plugin")
}

android {
    compileSdk  = AppConfig.compileSdkVersion
    defaultConfig {
        applicationId  = AppConfig.applicationId
        minSdk  = AppConfig.minSdkVersion
        targetSdk  = AppConfig.targetSdkVersion
        versionCode  = AppConfig.versionCode
        versionName  = AppConfig.versionName
        testInstrumentationRunner = AppConfig.testRunner
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    applicationVariants.all(ApplicationVariantAction())
}

dependencies {
    addFeatureModules()
    addDataModule()
    addDomainModule()
    addCommonModule()
    addAssetsModule()
    addSunmiPrinterModule()
    addBluetoothPrinterModule()
    addDateFormatterModule()
    addConnectivityModule()
    addPicassoDependencies()
    addLeakcanaryDependencies()
    addAndroidxCoreDependencies()
    addAndroidxLifeCycleDependencies()
    addHiltDependencies()
    addHiltWorkerDependencies()
    addRxjava3Dependencies()
    addRoomDependencies()
    addAndroidTestsDependencies()

}

kapt {
    correctErrorTypes = true
}

class ApplicationVariantAction : Action<com.android.build.gradle.api.ApplicationVariant> {
    override fun execute(variant: com.android.build.gradle.api.ApplicationVariant) {
        val fileName = createFileName(variant)
        variant.outputs.all(VariantOutputAction(fileName))
    }

    private fun createFileName(variant: com.android.build.gradle.api.ApplicationVariant): String {
        var variantName = variant.name
        if (variantName == "debug"){
            variantName = "dev"
        }
        else if (variantName == "release"){
            variantName = "live"
        }
        return project.name + "_${variantName}" + "_${variant.versionName}" + ".apk"
    }

    class VariantOutputAction(
        private val fileName: String
    ) : Action<com.android.build.gradle.api.BaseVariantOutput> {
        override fun execute(output: com.android.build.gradle.api.BaseVariantOutput) {
            if (output is com.android.build.gradle.internal.api.BaseVariantOutputImpl) {
                output.outputFileName = fileName
            }
        }
    }
}