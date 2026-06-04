package com.egoodhall.tools.wire.safe.enums.jackson

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer
import kotlin.reflect.KClass

internal class WireSafeEnumDeserializer<T : Enum<T>> :
  JsonDeserializer<WireSafeEnum<T>>(), ContextualDeserializer {

  private lateinit var type: JavaType

  @Suppress("UNCHECKED_CAST")
  override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): WireSafeEnum<T>? {
    if (parser.currentToken != JsonToken.VALUE_STRING) {
      ctxt.handleUnexpectedToken(type, parser)
      return null
    }

    val enumType = type.bindings.getBoundType(0)
    val enumClass = enumType.rawClass.kotlin as KClass<T>
    val delegate =
      ctxt.findRootValueDeserializer(enumType)
        ?: error("Unable to find deserializer for ${enumClass.simpleName}")
    return try {
      WireSafeEnum.of(delegate.deserialize(parser, ctxt) as T)
    } catch (_: Exception) {
      WireSafeEnum.of(parser.text!!)
    }
  }

  override fun createContextual(
    ctxt: DeserializationContext,
    property: BeanProperty?,
  ): JsonDeserializer<*> {
    val type = ctxt.contextualType ?: error("Unable to determine enum type from context")
    this.type = type
    return this
  }
}

internal class WireSafeEnumKeyDeserializer<T : Enum<T>> :
  KeyDeserializer(), ContextualKeyDeserializer {
  private lateinit var delegate: KeyDeserializer

  override fun deserializeKey(key: String?, ctxt: DeserializationContext): Any? {
    if (key == null) {
      return null
    }
    return try {
      @Suppress("UNCHECKED_CAST") WireSafeEnum.of(delegate.deserializeKey(key, ctxt) as T)
    } catch (_: Exception) {
      WireSafeEnum.of(key)
    }
  }

  override fun createContextual(
    ctxt: DeserializationContext,
    property: BeanProperty?,
  ): KeyDeserializer? {
    val type = ctxt.contextualType ?: error("Unable to determine enum type from context")
    val mapType = type.bindings.getBoundType(0)
    val keyType = mapType.bindings.getBoundType(0)
    this.delegate =
      ctxt.findKeyDeserializer(keyType, property)
        ?: error("Unable to find key deserializer for type $keyType")
    return this
  }
}
