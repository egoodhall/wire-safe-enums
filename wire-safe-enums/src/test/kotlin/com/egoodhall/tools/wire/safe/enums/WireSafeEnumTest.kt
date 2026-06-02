package com.egoodhall.tools.wire.safe.enums

import java.util.TreeSet
import java.util.concurrent.atomic.AtomicBoolean
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WireSafeEnumTest {

  enum class TestEnum {
    A,
    B,
    C,
  }

  @Test
  fun `it consistently sorts`() {
    val actual: TreeSet<WireSafeEnum<TestEnum>> =
      sortedSetOf(
        WireSafeEnum.of(TestEnum.C),
        WireSafeEnum.of("E"),
        WireSafeEnum.of(TestEnum.A),
        WireSafeEnum.of("D"),
        WireSafeEnum.of(TestEnum.B),
      )

    assertThat(actual)
      .containsExactly(
        TestEnum.A.wireSafe(),
        TestEnum.B.wireSafe(),
        TestEnum.C.wireSafe(),
        WireSafeEnum.of("D"),
        WireSafeEnum.of("E"),
      )
  }

  @Test
  fun `match picks known variant when known`() {
    val actual = WireSafeEnum.of(TestEnum.A).match(known = { true }, unknown = { false })

    assertThat(actual).isTrue
  }

  @Test
  fun `match picks unknown variant when unknown`() {
    val actual = WireSafeEnum.of("D").match(known = { false }, unknown = { true })

    assertThat(actual).isTrue
  }

  @Test
  fun `A is equal to A`() {
    val actual = TestEnum.A.wireSafe()
    val expected = TestEnum.A.wireSafe()
    assertThat(actual).isEqualTo(expected)
  }

  @Test
  fun `A is not equal to B`() {
    val actual = TestEnum.A.wireSafe()
    val other = TestEnum.B.wireSafe()
    assertThat(actual).isNotEqualTo(other)
  }

  @Test
  fun `A is not equal to "D"`() {
    val actual = TestEnum.A.wireSafe()
    val other = WireSafeEnum.of<TestEnum>("D")
    assertThat(actual).isNotEqualTo(other)
  }

  @Test
  fun `isKnown returns true when Known`() {
    assertThat(TestEnum.A.wireSafe().isKnown).isTrue
  }

  @Test
  fun `isKnown returns false when Unknown`() {
    assertThat(WireSafeEnum.of("D").isKnown).isFalse
  }

  @Test
  fun `asOptional returns empty when Unknown`() {
    assertThat(WireSafeEnum.of("D").asOptional()).isEmpty
  }

  @Test
  fun `asOptional returns present when Known`() {
    assertThat(TestEnum.A.wireSafe().asOptional()).isPresent.contains(TestEnum.A)
  }

  @Test
  fun `consume picks known variant when Known`() {
    val out = AtomicBoolean(false)
    TestEnum.A.wireSafe().consume(known = { out.set(true) }, unknown = { out.set(false) })
    assertThat(out.get()).isTrue()
  }

  @Test
  fun `consume picks unknown variant when Unknown`() {
    val out = AtomicBoolean(false)
    WireSafeEnum.of<TestEnum>("D").consume(known = { out.set(false) }, unknown = { out.set(true) })

    assertThat(out.get()).isTrue()
  }

  @Test
  fun `toString displays Unknown with string value`() {
    assertThat("${WireSafeEnum.of<TestEnum>("D")}").isEqualTo("Unknown[\"D\"]")
  }

  @Test
  fun `toString displays Known with enum class and value`() {
    assertThat("${TestEnum.A.wireSafe()}").isEqualTo("Known[TestEnum.A]")
  }

  @Test
  fun `isWireSafeEnum(Class) returns true for WireSafeEnum`() {
    val type = WireSafeEnum::class.java
    assertThat(WireSafeEnum.isWireSafeEnum(type)).isTrue
  }

  @Test
  fun `isWireSafeEnum(Class) returns true for Known`() {
    val type = TestEnum.A.wireSafe()::class.java
    assertThat(WireSafeEnum.isWireSafeEnum(type)).isTrue
  }

  @Test
  fun `isWireSafeEnum(Class) returns true for Unknown`() {
    val type = WireSafeEnum.of<TestEnum>("D")::class.java
    assertThat(WireSafeEnum.isWireSafeEnum(type)).isTrue
  }
}
