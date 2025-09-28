dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}


// Project modules
rootProject.name = "ktools"
include(":wire-safe-enums")
include("wire-safe-enums-jackson")
include("wire-safe-enums-kotlinx")
include("wire-safe-enums-moshi")