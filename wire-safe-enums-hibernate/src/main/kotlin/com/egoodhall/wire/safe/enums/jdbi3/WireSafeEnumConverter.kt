package com.egoodhall.wire.safe.enums.jdbi3

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import jakarta.persistence.AttributeConverter

abstract class WireSafeEnumConverter<T : Enum<T>>(private val enumClass: Class<T>) :
  AttributeConverter<WireSafeEnum<T>, String> {

  override fun convertToDatabaseColumn(attribute: WireSafeEnum<T>?): String? =
    attribute?.match(known = { it.name }, unknown = { it })

  override fun convertToEntityAttribute(dbData: String?): WireSafeEnum<T>? {
    if (dbData == null) return null
    return try {
      WireSafeEnum.of(java.lang.Enum.valueOf(enumClass, dbData))
    } catch (@Suppress("SwallowedException") e: IllegalArgumentException) {
      WireSafeEnum.of(dbData)
    }
  }
}
