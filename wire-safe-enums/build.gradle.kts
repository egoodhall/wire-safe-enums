plugins {
  id("buildsrc.convention.kotlin-jvm")
  kotlin("plugin.serialization") version "2.2.0"
}

group = "com.egoodhall"

version = "1.0-SNAPSHOT"

dependencies {
  implementation(libs.jackson.module.kotlin)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.annotations)
  implementation(libs.kotlinx.serialization.json)

  testImplementation(kotlin("test"))
  testImplementation(libs.assertj.core)
}
