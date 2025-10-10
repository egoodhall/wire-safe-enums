package buildsrc.convention.util

fun envVar(name: String): String =
  System.getenv(name)?.takeIf(String::isNotBlank)
    ?: error("Environment variable '$name' is missing")