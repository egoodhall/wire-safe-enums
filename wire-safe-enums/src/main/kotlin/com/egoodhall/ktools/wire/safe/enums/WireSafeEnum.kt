package com.egoodhall.ktools.wire.safe.enums

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import kotlinx.serialization.Serializable

@JsonSerialize(
  using = WireSafeEnumSerializer::class,
  keyUsing = WireSafeEnumKeySerializer::class,
  contentUsing = WireSafeEnumSerializer::class,
)
@JsonDeserialize(
  using = WireSafeEnumDeserializer::class,
  keyUsing = WireSafeEnumKeyDeserializer::class,
  contentUsing = WireSafeEnumDeserializer::class,
)
@Serializable(with = WireSafeEnumKSerializer::class)
sealed interface WireSafeEnum<T : Enum<T>> {
  fun unwrap(): T?

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
  data class Known<T : Enum<T>>(val value: T) : WireSafeEnum<T> {
    override fun unwrap(): T = value

    override fun toString(): String = "WireSafeEnum.Known($value)"
  }

  /**
   * Unknown implementation of WireSafeEnum. When deserializing from JSON, this value will be used
   * if the JSON string is for an enum value **not** known to this JVM
   */
  data class Unknown<T : Enum<T>>(val value: String) : WireSafeEnum<T> {
    override fun unwrap(): T? = null

    override fun toString(): String = "WireSafeEnum.Unknown(\"$value\")"
  }
}
