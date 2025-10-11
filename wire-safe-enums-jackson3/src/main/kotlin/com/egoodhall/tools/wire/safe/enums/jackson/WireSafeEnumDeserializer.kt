package com.egoodhall.tools.wire.safe.enums.jackson

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import kotlin.reflect.KClass
import tools.jackson.core.JsonParser
import tools.jackson.core.JsonToken
import tools.jackson.databind.BeanProperty
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JavaType
import tools.jackson.databind.ValueDeserializer

internal class WireSafeEnumDeserializer<T : Enum<T>> : ValueDeserializer<WireSafeEnum<T>>() {

  private lateinit var type: JavaType

  @Suppress("UNCHECKED_CAST")
  override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): WireSafeEnum<T>? {
    if (parser.currentToken() != JsonToken.VALUE_STRING) {
      ctxt.handleUnexpectedToken(type, parser)
      return null
    }

    val enumType = type.bindings.getBoundType(0)
    val enumClass = enumType.rawClass.kotlin as KClass<T>
    val delegate =
      ctxt.findRootValueDeserializer(enumType)
        ?: throw IllegalStateException("Unable to find deserializer for ${enumClass.simpleName}")
    return try {
      WireSafeEnum.of(delegate.deserialize(parser, ctxt) as T)
    } catch (_: Exception) {
      WireSafeEnum.of(parser.string!!)
    }
  }

  override fun createContextual(
    ctxt: DeserializationContext,
    property: BeanProperty?,
  ): ValueDeserializer<*> {
    val type =
      ctxt.contextualType
        ?: throw IllegalStateException("Unable to determine enum type from context")
    this.type = type
    return this
  }
}
