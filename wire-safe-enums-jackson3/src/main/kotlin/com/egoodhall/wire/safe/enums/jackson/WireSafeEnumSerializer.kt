package com.egoodhall.wire.safe.enums.jackson

import com.egoodhall.wire.safe.enums.WireSafeEnum
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer

internal class WireSafeEnumSerializer<T : Enum<T>> : ValueSerializer<WireSafeEnum<T>>() {
  override fun serialize(
    value: WireSafeEnum<T>?,
    gen: JsonGenerator,
    serializers: SerializationContext,
  ) {
    if (value == null) {
      gen.writeNull()
      return
    }

    value.consume(
      known = {
        val delegate = serializers.findValueSerializer(it::class.java)
        delegate.serialize(value.unwrap(), gen, serializers)
      },
      unknown = { gen.writeString(it) },
    )
  }
}
