package com.egoodhall.wire.safe.enums.moshi

import com.egoodhall.wire.safe.enums.WireSafeEnum
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.rawType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class WireSafeEnumAdapterFactory : JsonAdapter.Factory {
  override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
    return getEnumType(type)?.let { WireSafeEnumAdapter(moshi.adapter(it)) }
  }

  private fun <T : Enum<T>> getEnumType(from: Type): Class<T>? {
    if (from !is ParameterizedType || from.rawType != WireSafeEnum::class.java) {
      return null
    }

    @Suppress("UNCHECKED_CAST")
    return from.actualTypeArguments[0].rawType as Class<T>
  }
}

class WireSafeEnumAdapter<T : Enum<T>>(private val delegate: JsonAdapter<T>) :
  JsonAdapter<WireSafeEnum<T>>() {
  override fun fromJson(reader: JsonReader): WireSafeEnum<T>? {
    return try {
      delegate
        .fromJson(reader.peekJson())
        // If we were able to successfully read the value, we can
        // "forward" the reader to the next token. Since WireSafeEnum
        // is intended to handle unknown values, we don't want to call
        // skipName or skipValue, as that'll throw an exception with
        // certain configurations of the reader.
        ?.also { reader.nextSource() }
        ?.let { WireSafeEnum.of(it) }
    } catch (_: Exception) {
      WireSafeEnum.of(reader.nextString())
    }
  }

  override fun toJson(writer: JsonWriter, value: WireSafeEnum<T>?) {
    if (value == null) {
      writer.nullValue()
      return
    }

    value.consume(known = { delegate.toJson(writer, it) }, unknown = { writer.value(it) })
  }
}
