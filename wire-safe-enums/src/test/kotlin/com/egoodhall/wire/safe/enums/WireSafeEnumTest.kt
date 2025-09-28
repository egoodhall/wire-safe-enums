package com.egoodhall.wire.safe.enums

import java.util.TreeSet
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
        WireSafeEnum.known(TestEnum.C),
        WireSafeEnum.unknown("E"),
        WireSafeEnum.known(TestEnum.A),
        WireSafeEnum.unknown("D"),
        WireSafeEnum.known(TestEnum.B),
      )

    assertThat(actual)
      .containsExactly(
        TestEnum.A.wireSafe(),
        TestEnum.B.wireSafe(),
        TestEnum.C.wireSafe(),
        WireSafeEnum.unknown("D"),
        WireSafeEnum.unknown("E"),
      )
  }

  @Test
  fun `it matches known values`() {
    val actual = WireSafeEnum.known(TestEnum.A).match(known = { true }, unknown = { false })

    assertThat(actual).isTrue
  }

  @Test
  fun `it matches unknown values`() {
    val actual = WireSafeEnum.unknown("D").match(known = { false }, unknown = { true })

    assertThat(actual).isTrue
  }
}
