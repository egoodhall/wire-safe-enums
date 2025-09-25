package com.egoodhall.ktools.wire.safe.enums

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import kotlin.reflect.KClass

/** Adds a helper method to [ObjectMapper] for reading wire safe enums from JSON. */
inline fun <reified T : Enum<T>> ObjectMapper.readWireSafeEnum(value: String): WireSafeEnum<T> {
  return try {
    WireSafeEnum.Known(readValue(value, T::class.java))
  } catch (_: Exception) {
    WireSafeEnum.Unknown(value)
  }
}

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
        ?: throw IllegalStateException("Unable to find deserializer for ${enumClass.simpleName}")
    return try {
      WireSafeEnum.Known(delegate.deserialize(parser, ctxt) as T)
    } catch (_: Exception) {
      WireSafeEnum.Unknown(parser.text!!)
    }
  }

  override fun createContextual(
    ctxt: DeserializationContext,
    property: BeanProperty?,
  ): JsonDeserializer<*> {
    val type =
      ctxt.contextualType
        ?: throw IllegalStateException("Unable to determine enum type from context")
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
      @Suppress("UNCHECKED_CAST") WireSafeEnum.Known(delegate.deserializeKey(key, ctxt) as T)
    } catch (_: Exception) {
      WireSafeEnum.Unknown(key)
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
