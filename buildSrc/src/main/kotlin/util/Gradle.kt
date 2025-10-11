package buildsrc.convention.util

import org.gradle.api.Project

fun Project.rootProject(): Project {
  return parent?.rootProject() ?: this
}