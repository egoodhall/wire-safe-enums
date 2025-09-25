package com.egoodhall.ktools.wire.safe.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

inline fun <reified T : Enum<T>> Json.decodeWireSafeEnum(from: String): WireSafeEnum<T> {
  return Json.decodeFromString<WireSafeEnum<T>>(from)
}

internal class WireSafeEnumKSerializer<T : Enum<T>>(private val valueSerializer: KSerializer<T>) :
  KSerializer<WireSafeEnum<T>> {

  override val descriptor: SerialDescriptor =
    SerialDescriptor(
      "com.egoodhall.ktools.wire.safe.enums.WireSafeEnum",
      valueSerializer.descriptor,
    )

  override fun serialize(encoder: Encoder, value: WireSafeEnum<T>) {
    when (value) {
      is WireSafeEnum.Known<T> -> encoder.encodeSerializableValue(valueSerializer, value.value)
      is WireSafeEnum.Unknown -> encoder.encodeString(value.value)
    }
  }

  override fun deserialize(decoder: Decoder): WireSafeEnum<T> {
    val tree = decoder.decodeSerializableValue(JsonElement.serializer())
    return try {
      WireSafeEnum.Known(Json.decodeFromJsonElement(valueSerializer, tree))
    } catch (e: Exception) {
      WireSafeEnum.Unknown(Json.decodeFromJsonElement(String.serializer(), tree))
    }
  }
}
