package buildsrc.convention.util

fun envVar(name: String): String {
  return maybeEnvVar(name) ?: error("Environment variable '$name' is missing")
}

fun maybeEnvVar(name: String): String? {
  return System.getenv(name)?.takeIf(String::isNotBlank)
}

fun onlyInCI(vararg otherRequiredVars: String, block: () -> Unit) {
  if (maybeEnvVar("CI") != null && otherRequiredVars.map { maybeEnvVar(it) }.all { it != null }) {
    block()
  }
}
