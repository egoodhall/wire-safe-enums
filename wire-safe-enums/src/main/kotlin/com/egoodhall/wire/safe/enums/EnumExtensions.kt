package com.egoodhall.wire.safe.enums

inline fun <reified T : Enum<T>> T.wireSafe(): WireSafeEnum<T> {
  return WireSafeEnum.known(this)
}
