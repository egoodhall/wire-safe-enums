plugins {
  id("buildsrc.convention.kotlin-jvm")
  kotlin("plugin.serialization") version "2.2.0"
}

dependencies {
  api(project(":wire-safe-enums"))
  implementation(libs.bundles.kotlinx)
  implementation(kotlin("reflect"))
}
