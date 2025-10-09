plugins { id("buildsrc.convention.kotlin-jvm") }

kotlin {
  jvmToolchain {
    languageVersion = JavaLanguageVersion.of(17)
    vendor = JvmVendorSpec.ADOPTIUM
  }
}

dependencies {
  api(project(":wire-safe-enums"))
  implementation(libs.bundles.jackson3)

  testImplementation(project(":wire-safe-enums-assertj"))
}
