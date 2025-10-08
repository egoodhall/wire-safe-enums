package com.egoodhall.wire.safe.enums.kotlinx

import com.egoodhall.wire.safe.enums.WireSafeEnum
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
    value.consume(
      known = { encoder.encodeSerializableValue(delegate, it) },
      unknown = { encoder.encodeString(it) },
    )
  }

  override fun deserialize(decoder: Decoder): WireSafeEnum<T> {
    val tree = decoder.decodeSerializableValue(JsonElement.serializer())
    return try {
      WireSafeEnum.of(Json.decodeFromJsonElement(delegate, tree))
    } catch (_: Exception) {
      WireSafeEnum.of(Json.decodeFromJsonElement(String.serializer(), tree))
    }
  }
}
