package com.egoodhall.tools.wire.safe.enums.jackson

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import kotlin.jvm.java
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.BeanProperty
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer

internal class WireSafeEnumKeySerializer<T : Enum<T>> : ValueSerializer<WireSafeEnum<T>>() {
  private var property: BeanProperty? = null

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
        val serializer = serializers.findKeySerializer(it::class.java, property)
        serializer.serialize(value.unwrap(), gen, serializers)
      },
      unknown = { gen.writeName(it) },
    )
  }

  override fun createContextual(
    ctxt: SerializationContext?,
    property: BeanProperty?,
  ): ValueSerializer<*> {
    this.property = property
    return this
  }
}
