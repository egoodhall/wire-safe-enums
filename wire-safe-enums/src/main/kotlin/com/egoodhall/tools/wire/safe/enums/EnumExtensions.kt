package com.egoodhall.tools.wire.safe.enums

inline fun <reified T : Enum<T>> T.wireSafe(): WireSafeEnum<T> {
  return WireSafeEnum.of(this)
}
