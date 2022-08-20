package dependencies
import core.ModulesDep

internal val featureModuleDependencies = listOf(
    ModulesDep.splash,
    ModulesDep.login
)
internal val featureModuleDependantDependencies = listOf(
    ModulesDep.di,
    ModulesDep.domain,
    ModulesDep.assets,
    ModulesDep.common
)