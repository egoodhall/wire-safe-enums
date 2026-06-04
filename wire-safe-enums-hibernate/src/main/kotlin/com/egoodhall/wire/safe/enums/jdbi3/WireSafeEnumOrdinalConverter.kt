package com.egoodhall.wire.safe.enums.jdbi3

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import jakarta.persistence.AttributeConverter

abstract class WireSafeEnumOrdinalConverter<T : Enum<T>>(private val enumClass: Class<T>) :
  AttributeConverter<WireSafeEnum<T>, Int> {

  private val constants: Array<T> = enumClass.enumConstants

  override fun convertToDatabaseColumn(attribute: WireSafeEnum<T>?): Int? =
    attribute?.match(
      known = { it.ordinal },
      unknown = {
        throw IllegalArgumentException(
          "Cannot store Unknown WireSafeEnum<${enumClass.simpleName}> value as ordinal: \"$it\""
        )
      },
    )

  @Suppress("UseRequire")
  override fun convertToEntityAttribute(dbData: Int?): WireSafeEnum<T>? {
    if (dbData == null) return null
    if (dbData !in constants.indices) {
      throw IllegalArgumentException(
        "No ordinal $dbData in enum ${enumClass.simpleName} (valid range: 0..${constants.size - 1})"
      )
    }
    return WireSafeEnum.of(constants[dbData])
  }
}
