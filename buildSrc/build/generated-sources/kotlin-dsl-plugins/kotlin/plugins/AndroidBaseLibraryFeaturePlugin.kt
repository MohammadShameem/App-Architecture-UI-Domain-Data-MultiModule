package plugins


/**
 * Precompiled [android-base-library-feature.gradle.kts][plugins.Android_base_library_feature_gradle] script plugin.
 *
 * @see plugins.Android_base_library_feature_gradle
 */
class AndroidBaseLibraryFeaturePlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("plugins.Android_base_library_feature_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
