package com.egoodhall.wire.safe.enums.moshi

import com.egoodhall.ktools.wire.safe.enums.kotlinx.WireSafeEnum
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
    try {
      delegate.fromJson(reader.peekJson())
    } catch (_: Exception) {
      return WireSafeEnum.Unknown(reader.nextString())
    }
    return delegate.fromJson(reader)?.let { WireSafeEnum.of(it) }
  }

  override fun toJson(writer: JsonWriter, value: WireSafeEnum<T>?) {
    when (value) {
      is WireSafeEnum.Known<T> -> delegate.toJson(writer, value.value)
      is WireSafeEnum.Unknown<T> -> writer.value(value.value)
      null -> writer.nullValue()
    }
  }
}
