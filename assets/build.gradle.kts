import dependencies.addAndroidResponsiveSizeDependenciesDependencies
import dependencies.addAndroidxCoreDependencies

plugins {
    plugins.`android-base-library`
}
dependencies {
    addAndroidxCoreDependencies()
    addAndroidResponsiveSizeDependenciesDependencies()
}
