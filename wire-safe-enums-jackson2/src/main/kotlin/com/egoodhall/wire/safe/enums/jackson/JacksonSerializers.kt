package com.egoodhall.wire.safe.enums.jackson

import com.egoodhall.wire.safe.enums.WireSafeEnum
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
    if (value == null) {
      gen.writeNull()
      return
    }

    value.consume(
      known = {
        (serializers.findValueSerializer(it::class.java)
            ?: throw IllegalStateException("Unable to find serializer for type ${it::class.java}"))
          .serialize(value.unwrap(), gen, serializers)
      },
      unknown = { gen.writeString(it) },
    )
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
    if (value == null) {
      return gen.writeNull()
    }

    value.consume(
      known = {
        val serializer = serializers.findKeySerializer(it::class.java, property)!!
        serializer.serialize(value.unwrap(), gen, serializers)
      },
      unknown = { gen.writeFieldName(it) },
    )
  }

  override fun createContextual(
    prov: SerializerProvider,
    property: BeanProperty?,
  ): JsonSerializer<*> {
    this.property = property
    return this
  }
}
