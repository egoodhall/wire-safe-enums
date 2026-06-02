package buildsrc.convention

import buildsrc.convention.tasks.PromoteMavenArtifactTask
import buildsrc.convention.util.envVar
import buildsrc.convention.util.onlyInCI
import buildsrc.convention.util.rootProject
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  kotlin("jvm")
  id("com.diffplug.spotless")
  `java-library`
  `maven-publish`
  signing
}

group = "com.egoodhall.tools"

repositories {
  mavenCentral()
}

val jvmLanguageVersion = JavaLanguageVersion.of(17)
val jvmVendor = JvmVendorSpec.ADOPTIUM

kotlin {
  jvmToolchain {
    languageVersion = jvmLanguageVersion
    vendor = jvmVendor
  }
}

java {
  toolchain {
    languageVersion = jvmLanguageVersion
    vendor = jvmVendor
  }
  withSourcesJar()
  withJavadocJar()
}

sourceSets {
  main {
    java.srcDirs("src/main/java")
    kotlin.srcDirs("src/main/kotlin")
  }
  test {
    java.srcDirs("src/test/java")
    kotlin.srcDirs("src/test/kotlin")
  }
}

spotless {
  // Java source code formatting
  java {
    palantirJavaFormat().formatJavadoc(true).style("PALANTIR")
  }
  // Kotlin source code formatting
  kotlin {
    ktfmt().googleStyle().configure {
      it.setMaxWidth(100)
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setRemoveUnusedImports(true)
    }
  }
  // Gradle build script formatting
  kotlinGradle {
    ktfmt().googleStyle().configure {
      it.setMaxWidth(100)
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setRemoveUnusedImports(true)
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

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])

      pom {
        name = project.name
        description = "Wrapper class for enums that supports deserialization of unknown enum values"
        url = "https://github.com/egoodhall/wire-safe-enums"

        licenses {
          license {
            name = "The Apache License, Version 2.0"
            url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
          }
        }

        developers {
          developer {
            id = "egoodhall"
            name = "Eric Goodhall"
          }
        }

        scm {
          connection = "scm:git:git://github.com/egoodhall/wire-safe-enums.git"
          developerConnection = "scm:git:ssh://github.com:egoodhall/wire-safe-enums.git"
          url = "https://github.com/egoodhall/wire-safe-enums"
        }
      }
    }
  }

  repositories {
    mavenLocal()

    onlyInCI("OSSRH_USERNAME", "OSSRH_PASSWORD") {
      maven {
        name = "CentralStaging"
        url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2")
        credentials {
          username = envVar("OSSRH_USERNAME")
          password = envVar("OSSRH_PASSWORD")
        }
      }
    }
  }
}

signing {
  onlyInCI("GPG_KEY", "GPG_KEY_PASSPHRASE") {
    useInMemoryPgpKeys(envVar("GPG_KEY"), envVar("GPG_KEY_PASSPHRASE"))
  }

  sign(publishing.publications["maven"])
}

onlyInCI {
  project.rootProject()
    .takeIf { it.tasks.withType<PromoteMavenArtifactTask>().isEmpty() }
    ?.tasks?.register<PromoteMavenArtifactTask>("promoteStagedMavenArtifactsToCentralRepository")
    ?.configure {
      group = "Publishing"
      description = "Promote staged Maven artifacts to OSSRH"
      dependsOn(
        tasks.withType<Sign>(),
        tasks.withType<PublishToMavenRepository>()
      )
    }
}
