import dependencies.*

plugins {
    plugins.`android-base-library-core`
}

dependencies {
    addSharedPrefModule(configurationName = "api")
    addNetworkDependencies()
    addTimberDependencies(configurationName = "api")
}