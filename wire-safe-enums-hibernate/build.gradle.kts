plugins { id("buildsrc.convention.kotlin-jvm") }

dependencies {
  api(project(":wire-safe-enums"))
  implementation(libs.hibernate.core)

  testImplementation(project(":wire-safe-enums-assertj"))
  testImplementation(libs.hibernate.community.dialects)
  testImplementation(libs.sqlite)
}
