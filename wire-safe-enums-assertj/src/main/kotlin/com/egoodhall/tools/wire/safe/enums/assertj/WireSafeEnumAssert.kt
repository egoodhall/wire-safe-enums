package com.egoodhall.tools.wire.safe.enums.assertj

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import kotlin.reflect.KClass
import kotlin.reflect.typeOf
import org.assertj.core.api.InstanceOfAssertFactory
import org.assertj.core.api.ObjectAssert
import org.assertj.core.api.StringAssert

fun <T : Enum<T>> assertThat(actual: WireSafeEnum<T>): WireSafeEnumAssert<T> {
  return WireSafeEnumAssert(actual)
}

class WireSafeEnumAssert<T : Enum<T>>(actual: WireSafeEnum<T>) :
  ObjectAssert<WireSafeEnum<T>>(actual) {
  companion object {
    inline fun <reified T : Enum<T>> factory():
      InstanceOfAssertFactory<WireSafeEnum<T>, WireSafeEnumAssert<T>> {
      @Suppress("UNCHECKED_CAST")
      val type = typeOf<WireSafeEnum<T>>().classifier as KClass<WireSafeEnum<T>>
      return InstanceOfAssertFactory(type.java) { t -> WireSafeEnumAssert(t) }
    }
  }

  fun isKnown(): WireSafeEnumAssert<T> =
    actual.match(
      known = { this },
      unknown = {
        failWithMessage("expected known enum value, but was $it")
        error("unreachable")
      },
    )

  fun containsKnown(expected: T): WireSafeEnumAssert<T> =
    actual.match(
      known = {
        if (expected != it) {
          failWithMessage("expected enum value $expected, but was $it")
        }
        this
      },
      unknown = {
        failWithMessage("expected known enum value, but was $it")
        error("unreachable")
      },
    )

  fun extractingKnown(): ObjectAssert<T> =
    actual.match(
      known = { ObjectAssert(it) },
      unknown = {
        failWithMessage("expected known enum value, but was $actual")
        error("unreachable")
      },
    )

  fun isUnknown(): WireSafeEnumAssert<T> =
    actual.match(
      known = {
        failWithMessage("expected unknown value, but it was $actual")
        error("unreachable")
      },
      unknown = { this },
    )

  fun containsUnknown(expected: String): WireSafeEnumAssert<T> =
    actual.match(
      known = {
        failWithMessage("expected unknown value, but it was \"$actual\"")
        error("unreachable")
      },
      unknown = {
        if (expected != it) {
          failWithMessage("expected value \"$expected\", but was \"$it\"")
        }
        this
      },
    )

  fun extractingUnknown(): StringAssert =
    actual.match(
      known = {
        failWithMessage("expected unknown value, but it was $actual")
        error("unreachable")
      },
      unknown = { StringAssert(it) },
    )
}
