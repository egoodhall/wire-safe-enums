package com.egoodhall.wire.safe.enums;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

public class WireSafeEnumJavaTest {

    enum TestEnum {
        A,
        B,
        C,
    }

    @Test
    void itConsistentlySorts() {
        TreeSet<WireSafeEnum<TestEnum>> actual = new TreeSet<>(List.of(
                WireSafeEnum.of(TestEnum.C),
                WireSafeEnum.of("E"),
                WireSafeEnum.of(TestEnum.A),
                WireSafeEnum.of("D"),
                WireSafeEnum.of(TestEnum.B)));

        assertThat(actual)
                .containsExactly(
                        WireSafeEnum.of(TestEnum.A),
                        WireSafeEnum.of(TestEnum.B),
                        WireSafeEnum.of(TestEnum.C),
                        WireSafeEnum.of("D"),
                        WireSafeEnum.of("E"));
    }

    @Test
    void itMatchesKnownValues() {
        boolean actual = WireSafeEnum.of(TestEnum.A).match((known) -> true, (unknown) -> false);

        assertThat(actual).isTrue();
    }

    @Test
    void itMatchesUnknownValues() {
        boolean actual = WireSafeEnum.of("D").match((known) -> false, (unknown) -> true);

        assertThat(actual).isTrue();
    }

  @Test
  void itConvertsToOptional() {
      Optional<TestEnum> actual = WireSafeEnum.of(TestEnum.A).asOptional();

      assertThat(actual).isPresent();
  }
}
