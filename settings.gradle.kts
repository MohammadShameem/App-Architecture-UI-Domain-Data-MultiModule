pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        google()
        maven ("https://jitpack.io")
        maven ("https://oss.jfrog.org/libs-snapshot")
    }
}


rootProject.name = "App-Architecture-UI-Domain-Data-MultiModule"
include (":app")
include (":assets")
include (":di")
include (":library:sharedpref")
include (":library:sunmiprinter")
include (":library:bluetoothprinter")
include (":library:connectivity")
include (":library:dateformatter")

include (":model:apiresponse")
include (":model:entity")
include (":domain")
include (":data")
include (":common")
include (":cache")

include(":features:profile")
include(":features:login")
include(":features:dashboard")
include(":features:settings")
include(":features:splash")
