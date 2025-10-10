package buildsrc.convention

import buildsrc.convention.tasks.PromoteMavenArtifactTask
import buildsrc.convention.util.envVar
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

val jvmLanguageVersion = JavaLanguageVersion.of(11)
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
    resources.srcDirs("src/main/resources")
  }
  test {
    java.srcDirs("src/test/java")
    kotlin.srcDirs("src/test/kotlin")
    resources.srcDirs("src/test/resources")
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
        url = "https://github.com/egoodhall/ktools"

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
    notInCI {
      mavenLocal()
    }

    onlyInCI {
      // Maven central
      maven {
        name = "CentralStaging"
        url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2")
        credentials {
          username = System.getenv("OSSRH_USERNAME")
          password = System.getenv("OSSRH_PASSWORD")
        }
      }

      // GitHub Packages
      maven {
        name = "GithubPackages"
        url = uri("https://maven.pkg.github.com/egoodhall/wire-safe-enums")
        credentials {
          username = System.getenv("GITHUB_ACTOR")
          password = System.getenv("GITHUB_TOKEN")
        }
      }
    }
  }
}

onlyInCI {
  signing {
    val signingKey = envVar("SIGNING_KEY")
    val signingPassword = envVar("SIGNING_PASSWORD")

    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["maven"])
  }
}

onlyInCI {
  tasks.register<PromoteMavenArtifactTask>("promoteStagedMavenArtifactsToCentralRepository").configure {
    group = "Publishing"
    description = "Promote staged Maven artifacts to OSSRH"
    dependsOn(tasks.named("publishAllPublicationsToCentralStagingRepository"))
  }
}

/////////////////////////////
// CI configuration gating //
/////////////////////////////

fun isInCI(): Boolean = System.getenv("CI")?.takeIf(String::isNotBlank) != null

fun onlyInCI(block: () -> Unit) {
  if (isInCI()) {
    block()
  }
}

fun notInCI(block: () -> Unit) {
  if (!isInCI()) {
    block()
  }
}
