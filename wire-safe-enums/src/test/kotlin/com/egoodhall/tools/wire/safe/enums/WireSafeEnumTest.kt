package com.egoodhall.tools.wire.safe.enums

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
  fun `it matches known values`() {
    val actual = WireSafeEnum.of(TestEnum.A).match(known = { true }, unknown = { false })

    assertThat(actual).isTrue
  }

  @Test
  fun `it matches unknown values`() {
    val actual = WireSafeEnum.of("D").match(known = { false }, unknown = { true })

    assertThat(actual).isTrue
  }
}
