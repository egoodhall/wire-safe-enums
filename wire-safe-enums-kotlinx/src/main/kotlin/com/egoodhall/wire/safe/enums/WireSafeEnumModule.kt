package com.egoodhall.wire.safe.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val WireSafeEnumModule = SerializersModule {
  contextual(WireSafeEnum::class) { wireSafeEnumSerializer(it[0]) }
  polymorphic(WireSafeEnum::class) {
    contextual(Known::class) { wireSafeEnumSerializer(it[0]) }
    contextual(Unknown::class) { wireSafeEnumSerializer(it[0]) }
  }
}

private fun <T : Enum<T>> wireSafeEnumSerializer(
  delegate: KSerializer<*>
): WireSafeEnumSerializer<T> {
  @Suppress("UNCHECKED_CAST")
  return WireSafeEnumSerializer(delegate as KSerializer<T>)
}
