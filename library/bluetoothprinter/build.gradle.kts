import dependencies.*

plugins {
    plugins.`android-base-library`
}


dependencies{
    addAndroidxLifeCycleDependencies()
    addEntityModule()
    addCommonModule()
    addDateFormatterModule()
    addSharedPrefModule()
}