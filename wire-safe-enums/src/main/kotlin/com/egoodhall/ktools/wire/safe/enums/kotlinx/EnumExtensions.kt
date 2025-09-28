package com.egoodhall.ktools.wire.safe.enums.kotlinx

inline fun <reified T : Enum<T>> T.wireSafe(): WireSafeEnum<T> {
  return WireSafeEnum.Known(this)
}
