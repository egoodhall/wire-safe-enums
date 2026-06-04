dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}


// Project modules
rootProject.name = "wire-safe-enums-parent"
include(":wire-safe-enums")
include("wire-safe-enums-assertj")
include("wire-safe-enums-hibernate")
include("wire-safe-enums-jackson2")
include("wire-safe-enums-jackson3")
include("wire-safe-enums-jdbi3")
include("wire-safe-enums-kotlinx")
include("wire-safe-enums-moshi")