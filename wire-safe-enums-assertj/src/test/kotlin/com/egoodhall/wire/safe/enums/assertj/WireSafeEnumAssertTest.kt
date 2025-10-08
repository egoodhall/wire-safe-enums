package com.egoodhall.wire.safe.enums.assertj

import com.egoodhall.wire.safe.enums.WireSafeEnum
import com.egoodhall.wire.safe.enums.wireSafe
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class WireSafeEnumAssertTest {

  enum class TeeShirtSize {
    SM,
    MD,
    LG,
  }

  @Test
  fun `it asserts that type is known`() {
    val actual = WireSafeEnum.of(TeeShirtSize.MD)

    assertThat(actual).isKnown()
    assertThat(actual).containsKnown(TeeShirtSize.MD)
    assertThat(actual).extractingKnown().isEqualTo(TeeShirtSize.MD).isNotEqualTo(TeeShirtSize.LG)
  }

  @Test
  fun `it asserts that type is unknown`() {
    val actual = WireSafeEnum.of("XL")

    assertThat(actual).isUnknown()
    assertThat(actual).containsUnknown("XL")
    assertThat(actual).extractingUnknown().isEqualTo("XL")
  }

  @Test
  fun `it has working InstanceOfAssertFactory`() {
    Assertions.assertThat(TeeShirtSize.SM.wireSafe())
      .asInstanceOf(WireSafeEnumAssert.factory<TeeShirtSize>())
      .containsKnown(TeeShirtSize.SM)
  }
}
