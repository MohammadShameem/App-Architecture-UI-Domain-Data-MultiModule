import dependencies.*

plugins {
    plugins.`android-base-library`
}


dependencies{
    addSunmiPrinterDependencies()
    addEntityModule()
    addCommonModule()
    addDateFormatterModule()
    addSharedPrefModule()
}