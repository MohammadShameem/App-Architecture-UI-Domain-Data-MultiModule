import dependencies.*

plugins {
    plugins.`android-base-library`
}
dependencies {
    addAndroidxCoreDependencies()
    addAndroidxLifeCycleDependencies()
    addRxjava3Dependencies()
    addPlayCoreDependencies()
    addEntityModule()
    addSharedPrefModule()
    addDateFormatterModule()
}
