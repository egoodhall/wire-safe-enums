package com.egoodhall.wire.safe.enums

sealed class WireSafeEnum<T : Enum<T>> : Comparable<WireSafeEnum<T>> {
  companion object {
    @JvmStatic fun <T : Enum<T>> known(value: T): WireSafeEnum<T> = Known(value)

    @JvmStatic fun <T : Enum<T>> unknown(value: String): WireSafeEnum<T> = Unknown(value)
  }

  fun <U> match(known: (T) -> U, unknown: (String) -> U) =
    when (this) {
      is Known<T> -> known(value)
      is Unknown<T> -> unknown(value)
    }

  fun isKnown(): Boolean = match(known = { true }, unknown = { false })

  fun unwrap(): T? = match(known = { it }, unknown = { null })

  fun consume(known: (T) -> Unit, unknown: (String) -> Unit) = match(known, unknown)
}

/**
 * Known implementation of WireSafeEnum. When deserializing from JSON, this value will be used if
 * the JSON string is for an enum value known to this JVM
 */
data class Known<T : Enum<T>>(val value: T) : WireSafeEnum<T>() {
  override fun toString(): String = "WireSafeEnum.Known(${value::class.simpleName}.$value)"

  override fun compareTo(other: WireSafeEnum<T>): Int =
    other.match(known = { value.compareTo(it) }, unknown = { -1 })
}

/**
 * Unknown implementation of WireSafeEnum. When deserializing from JSON, this value will be used if
 * the JSON string is for an enum value **not** known to this JVM
 */
data class Unknown<T : Enum<T>>(val value: String) : WireSafeEnum<T>() {
  override fun toString(): String = "WireSafeEnum.Unknown(\"$value\")"

  override fun compareTo(other: WireSafeEnum<T>): Int =
    other.match(known = { 1 }, unknown = { value.compareTo(it) })
}
