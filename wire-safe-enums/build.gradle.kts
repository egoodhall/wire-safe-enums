plugins { id("buildsrc.convention.kotlin-jvm") }

group = "com.egoodhall"

version = "1.0-SNAPSHOT"

dependencies {
  implementation(libs.jackson.module.kotlin)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.annotations)

  testImplementation(kotlin("test"))
  testImplementation(libs.assertj.core)
}
