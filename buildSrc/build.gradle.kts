plugins {
  // The Kotlin DSL plugin provides a convenient way to develop convention plugins.
  // Convention plugins are located in `src/main/kotlin`, with the file extension `.gradle.kts`,
  // and are applied in the project's `build.gradle.kts` files as required.
  `kotlin-dsl`
  alias(libs.plugins.spotless)
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  // Add a dependency on the Kotlin Gradle plugin, so that convention plugins can apply it.
  implementation(libs.kotlin.gradle.plugin)
  implementation(plugin(libs.plugins.spotless))
}

spotless {
  kotlinGradle {
    ktfmt().googleStyle().configure {
      it.setMaxWidth(128)
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setRemoveUnusedImports(true)
      it.setManageTrailingCommas(true)
    }
  }
}

// Helper function that transforms a Gradle Plugin alias from a
// Version Catalog into a valid dependency notation for buildSrc
fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>) =
  plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }.get()
