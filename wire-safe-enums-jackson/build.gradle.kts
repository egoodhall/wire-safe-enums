plugins { id("buildsrc.convention.kotlin-jvm") }

dependencies {
  api(project(":wire-safe-enums"))
  implementation(libs.jackson.module.kotlin)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.annotations)

  testImplementation(kotlin("test"))
  testImplementation(libs.assertj.core)
}
