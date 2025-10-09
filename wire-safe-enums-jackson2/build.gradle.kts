plugins { id("buildsrc.convention.kotlin-jvm") }

dependencies {
  api(project(":wire-safe-enums"))
  implementation(libs.bundles.jackson2)

  testImplementation(project(":wire-safe-enums-assertj"))
}
