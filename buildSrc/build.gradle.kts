plugins {
  `kotlin-dsl`
  alias(libs.plugins.detekt)
  alias(libs.plugins.spotless)
}

kotlin {
  jvmToolchain(21)
}

dependencies {
  implementation(libs.kotlin.gradle.plugin)
  implementation(plugin(libs.plugins.detekt))
  implementation(plugin(libs.plugins.spotless))
}

detekt {
  buildUponDefaultConfig = true
}

spotless {
  kotlin {
    ktfmt().googleStyle().configure {
      it.setMaxWidth(128)
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setRemoveUnusedImports(true)
    }
  }
  kotlinGradle {
    ktfmt().googleStyle().configure {
      it.setMaxWidth(128)
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setRemoveUnusedImports(true)
    }
  }
}

// Helper function that transforms a Gradle Plugin alias from a
// Version Catalog into a valid dependency notation for buildSrc
fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>) =
  plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }.get()
