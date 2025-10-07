plugins { id("buildsrc.convention.kotlin-jvm") }

dependencies {
  api(project(":wire-safe-enums"))
  implementation(libs.bundles.jackson)

  testImplementation(project(":wire-safe-enums-assertj"))
}
