package dependencies
import  core.ModulesDep
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

fun DependencyHandler.addDiModule(configurationName:String = "implementation"){
    add(configurationName, project(ModulesDep.di))
}

fun DependencyHandler.addDomainModule(){
    add("implementation", project(ModulesDep.domain))
}

fun DependencyHandler.addDataModule(){
    add("implementation", project(ModulesDep.data))
}

fun DependencyHandler.addSunmiPrinterModule(){
    add("implementation", project(ModulesDep.sunmiPrinter))
}

fun DependencyHandler.addBluetoothPrinterModule(){
    add("implementation", project(ModulesDep.bluetoothPrinter))
}

fun DependencyHandler.addDateFormatterModule(){
    add("implementation", project(ModulesDep.dateFormatter))
}
fun DependencyHandler.addConnectivityModule(){
    add("implementation", project(ModulesDep.connectivity))
}

fun DependencyHandler.addSharedPrefModule(configurationName:String = "implementation"){
    add(configurationName, project(ModulesDep.sharedPref))
}

fun DependencyHandler.addCacheModule(configurationName:String = "implementation"){
    add(configurationName, project(ModulesDep.cache))
}

fun DependencyHandler.addApiResponseModule(configurationName:String = "implementation"){
    add(configurationName, project(ModulesDep.apiResponse))
}

fun DependencyHandler.addEntityModule(configurationName:String = "implementation"){
    add(configurationName, project(ModulesDep.entity))
}

fun DependencyHandler.addCommonModule(){
    add("implementation", project(ModulesDep.common))
}

fun DependencyHandler.addAssetsModule(){
    add("implementation", project(ModulesDep.assets))
}



fun DependencyHandler.addFeatureModules(){
   featureModuleDependencies.forEach{
       add("implementation", project(it))
   }
}


fun DependencyHandler.addFeatureModuleDependantDependencies(){
    featureModuleDependantDependencies.forEach{
        add("implementation", project(it))
    }
}


