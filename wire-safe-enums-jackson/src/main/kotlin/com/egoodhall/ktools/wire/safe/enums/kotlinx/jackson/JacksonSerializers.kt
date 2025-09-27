package com.egoodhall.ktools.wire.safe.enums.kotlinx.jackson

import com.egoodhall.ktools.wire.safe.enums.kotlinx.WireSafeEnum
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.ContextualSerializer

internal class WireSafeEnumSerializer<T : Enum<T>> : JsonSerializer<WireSafeEnum<T>>() {
  override fun serialize(
    value: WireSafeEnum<T>?,
    gen: JsonGenerator,
    serializers: SerializerProvider,
  ) {
    when (value) {
      is WireSafeEnum.Known<T> -> {
        val delegate =
          serializers.findValueSerializer(value.unwrap()::class.java)
            ?: throw IllegalStateException(
              "Unable to find serializer for type ${value.unwrap()::class.java}"
            )
        delegate.serialize(value.unwrap(), gen, serializers)
      }
      is WireSafeEnum.Unknown<T> -> gen.writeString(value.value)
      null -> gen.writeNull()
    }
  }
}

internal class WireSafeEnumKeySerializer<T : Enum<T>> :
  JsonSerializer<WireSafeEnum<T>>(), ContextualSerializer {
  private var property: BeanProperty? = null

  override fun serialize(
    value: WireSafeEnum<T>?,
    gen: JsonGenerator,
    serializers: SerializerProvider,
  ) {
    when (value) {
      is WireSafeEnum.Known<T> -> {
        val held = value.unwrap()
        val delegate =
          serializers.findKeySerializer(held::class.java, property)
            ?: throw IllegalStateException("Unable to find serializer for type ${held::class.java}")
        delegate.serialize(value.unwrap(), gen, serializers)
      }
      is WireSafeEnum.Unknown<T> -> gen.writeFieldName(value.value)
      null -> gen.writeNull()
    }
  }

  override fun createContextual(
    prov: SerializerProvider,
    property: BeanProperty?,
  ): JsonSerializer<*> {
    this.property = property
    return this
  }
}
