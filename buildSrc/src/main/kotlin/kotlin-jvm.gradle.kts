// The code in this file is a convention plugin - a Gradle mechanism for sharing reusable build logic.
// `buildSrc` is a Gradle-recognized directory and every plugin there will be easily available in the rest of the build.
package buildsrc.convention

import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  // Apply the Kotlin JVM plugin to add support for Kotlin in JVM projects.
  kotlin("jvm")
  id("com.diffplug.spotless")
}

repositories {
  mavenCentral()
}

kotlin {
  // Use a specific Java version to make it easier tod work in different environments.
  jvmToolchain(21)
}

spotless {
  kotlin {
    ktfmt().googleStyle().configure {
      it.setMaxWidth(100)
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setRemoveUnusedImports(true)
      it.setManageTrailingCommas(true)
    }
  }
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

tasks.withType<Test>().configureEach {
  // Configure all test Gradle tasks to use JUnitPlatform.
  useJUnitPlatform()

  // Log information about all test results, not only the failed ones.
  testLogging {
    events(
      TestLogEvent.FAILED,
      TestLogEvent.PASSED,
      TestLogEvent.SKIPPED
    )
  }
}
