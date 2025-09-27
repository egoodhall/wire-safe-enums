package com.egoodhall.ktools.wire.safe.enums.kotlinx

sealed abstract class WireSafeEnum<T : Enum<T>> {
  abstract fun unwrap(): T?

  companion object {
    @JvmStatic
    fun <T : Enum<T>> of(value: T): WireSafeEnum<T> {
      return Known(value)
    }
  }

  /**
   * Known implementation of WireSafeEnum. When deserializing from JSON, this value will be used if
   * the JSON string is for an enum value known to this JVM
   */
  data class Known<T : Enum<T>>(val value: T) : WireSafeEnum<T>() {
    override fun unwrap(): T = value

    override fun toString(): String = "WireSafeEnum.Known(${value::class.simpleName}.$value)"
  }

  /**
   * Unknown implementation of WireSafeEnum. When deserializing from JSON, this value will be used
   * if the JSON string is for an enum value **not** known to this JVM
   */
  data class Unknown<T : Enum<T>>(val value: String) : WireSafeEnum<T>() {
    override fun unwrap(): T? = null

    override fun toString(): String = "WireSafeEnum.Unknown(\"$value\")"
  }
}
