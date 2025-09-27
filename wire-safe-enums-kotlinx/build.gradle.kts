plugins {
  id("buildsrc.convention.kotlin-jvm")
  kotlin("plugin.serialization") version "2.2.0"
}

dependencies {
  api(project(":wire-safe-enums"))
  implementation(libs.kotlinx.serialization.json)

  testImplementation(kotlin("test"))
  testImplementation(libs.assertj.core)
}
