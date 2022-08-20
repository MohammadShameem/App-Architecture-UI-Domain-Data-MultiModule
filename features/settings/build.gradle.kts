import dependencies.addCircleImageviewDependencies
import dependencies.addFirebaseDependencies
import dependencies.addPicassoDependencies

plugins {
    plugins.`android-base-library-feature`
}

dependencies{
    addPicassoDependencies()
    addCircleImageviewDependencies()
    addFirebaseDependencies()
}