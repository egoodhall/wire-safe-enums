plugins { id("buildsrc.convention.kotlin-jvm") }

dependencies {
  api(project(":wire-safe-enums"))
  implementation(libs.jdbi3.core)

  testImplementation(project(":wire-safe-enums-assertj"))
  testImplementation(libs.jdbi3.kotlin)
  testImplementation(libs.jdbi3.kotlin.sqlobject)
  testImplementation(libs.jdbi3.testing)
  testImplementation(libs.liquibase.core)
  testImplementation(libs.sqlite)
}
