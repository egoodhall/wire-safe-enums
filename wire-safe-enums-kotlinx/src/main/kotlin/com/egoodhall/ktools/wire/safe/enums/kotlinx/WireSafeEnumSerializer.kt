package com.egoodhall.ktools.wire.safe.enums.kotlinx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

internal class WireSafeEnumSerializer<T : Enum<T>>(private val delegate: KSerializer<T>) :
  KSerializer<WireSafeEnum<T>> {

  override val descriptor: SerialDescriptor =
    SerialDescriptor("com.egoodhall.ktools.wire.safe.enums.WireSafeEnum", delegate.descriptor)

  override fun serialize(encoder: Encoder, value: WireSafeEnum<T>) {
    when (value) {
      is WireSafeEnum.Known<T> -> encoder.encodeSerializableValue(delegate, value.value)
      is WireSafeEnum.Unknown -> encoder.encodeString(value.value)
    }
  }

  override fun deserialize(decoder: Decoder): WireSafeEnum<T> {
    val tree = decoder.decodeSerializableValue(JsonElement.serializer())
    return try {
      WireSafeEnum.Known(Json.decodeFromJsonElement(delegate, tree))
    } catch (_: Exception) {
      WireSafeEnum.Unknown(Json.decodeFromJsonElement(String.serializer(), tree))
    }
  }
}
