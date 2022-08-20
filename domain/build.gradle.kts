import dependencies.*

plugins {
    plugins.`android-base-library-core`
}
dependencies {
    addCoroutinesAndroidDependencies()
    addSharedPrefModule(configurationName = "implementation")
    addEntityModule(configurationName = "api")
    addDateFormatterModule()

}