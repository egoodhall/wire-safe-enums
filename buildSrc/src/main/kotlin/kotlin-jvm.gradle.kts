// The code in this file is a convention plugin - a Gradle mechanism for sharing reusable build logic.
// `buildSrc` is a Gradle-recognized directory and every plugin there will be easily available in the rest of the build.
package buildsrc.convention

import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  kotlin("jvm")
  id("com.diffplug.spotless")
}

group = "com.egoodhall"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

kotlin {
  jvmToolchain(21)
}

spotless {
  // Kotlin source code formatting
  kotlin {
    ktfmt().googleStyle().configure {
      it.setMaxWidth(100)
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setRemoveUnusedImports(true)
      it.setManageTrailingCommas(true)
    }
  }
  // Gradle build script formatting
  kotlinGradle {
    ktfmt().googleStyle().configure {
      it.setMaxWidth(100)
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setRemoveUnusedImports(true)
      it.setManageTrailingCommas(true)
    }
  }
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
tasks.withType<Test>().configureEach {
  useJUnitPlatform()

  dependencies {
    testImplementation(libs.findLibrary("assertj-core").get())
    testImplementation(kotlin("test"))
  }

  testLogging {
    events(
      TestLogEvent.FAILED,
      TestLogEvent.PASSED,
      TestLogEvent.SKIPPED
    )
  }
}
