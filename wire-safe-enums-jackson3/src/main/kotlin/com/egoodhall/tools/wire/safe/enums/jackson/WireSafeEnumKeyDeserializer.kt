package com.egoodhall.tools.wire.safe.enums.jackson

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import tools.jackson.databind.BeanProperty
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.KeyDeserializer
import tools.jackson.databind.deser.ContextualKeyDeserializer

internal class WireSafeEnumKeyDeserializer<T : Enum<T>> :
  KeyDeserializer(), ContextualKeyDeserializer {
  private lateinit var delegate: KeyDeserializer

  override fun deserializeKey(key: String?, ctxt: DeserializationContext): Any? {
    if (key == null) {
      return null
    }
    return try {
      @Suppress("UNCHECKED_CAST") WireSafeEnum.Companion.of(delegate.deserializeKey(key, ctxt) as T)
    } catch (_: Exception) {
      WireSafeEnum.Companion.of(key)
    }
  }

  override fun createContextual(
    ctxt: DeserializationContext,
    property: BeanProperty?,
  ): KeyDeserializer? {
    val type =
      ctxt.contextualType
        ?: throw IllegalStateException("Unable to determine enum type from context")
    val mapType = type.bindings.getBoundType(0)
    val keyType = mapType.bindings.getBoundType(0)
    this.delegate =
      ctxt.findKeyDeserializer(keyType, property)
        ?: throw IllegalStateException("Unable to find key deserializer for type $keyType")
    return this
  }
}
