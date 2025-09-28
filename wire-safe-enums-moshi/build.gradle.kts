plugins { id("buildsrc.convention.kotlin-jvm") }

dependencies {
  api(project(":wire-safe-enums"))
  implementation(libs.bundles.moshi)
}
