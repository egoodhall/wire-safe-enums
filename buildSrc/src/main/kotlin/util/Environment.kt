package buildsrc.convention.util

fun envVar(name: String): String {
  return maybeEnvVar(name) ?: error("Environment variable '$name' is missing")
}

fun maybeEnvVar(name: String): String? {
  return System.getenv(name)?.takeIf(String::isNotBlank)
}

fun isInCI(): Boolean = try {
  envVar("CI")
  true
} catch (_: Throwable) {
  false
}

fun onlyWhenEnvVarsSet(vararg vars: String, block: () -> Unit) {
  if (vars.all { maybeEnvVar(it) != null}) {
    block()
  }
}

fun onlyInCI(block: () -> Unit) = if (isInCI()) block() else Unit

fun notInCI(block: () -> Unit) = if (!isInCI()) block() else Unit