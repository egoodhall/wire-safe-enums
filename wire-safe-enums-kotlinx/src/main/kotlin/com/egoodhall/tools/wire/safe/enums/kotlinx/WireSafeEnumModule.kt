package com.egoodhall.tools.wire.safe.enums.kotlinx

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val WireSafeEnumModule = SerializersModule {
  contextual(WireSafeEnum::class) { wireSafeEnumSerializer(it[0]) }
  polymorphic(WireSafeEnum::class) {
    WireSafeEnum::class.sealedSubclasses.forEach {
      contextual(it) { wireSafeEnumSerializer(it[0]) }
    }
  }
}

private fun <T : Enum<T>> wireSafeEnumSerializer(
  delegate: KSerializer<*>
): WireSafeEnumSerializer<T> {
  @Suppress("UNCHECKED_CAST")
  return WireSafeEnumSerializer(delegate as KSerializer<T>)
}
