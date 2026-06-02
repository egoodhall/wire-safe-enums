package com.egoodhall.wire.safe.enums.jdbi3

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import com.egoodhall.tools.wire.safe.enums.assertj.assertThat
import com.egoodhall.tools.wire.safe.enums.wireSafe
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test

class WireSafeEnumPluginTest : WireSafeEnumJdbiTest() {

  @Test
  fun `it roundtrips Known via text column`() = runWithRollback<MyTableDao> { dao ->
    val input = StringTableInput(StringType.TYPE_A.wireSafe())

    val row = dao.insertString(input).let { dao.getString(it) }
      ?: throw AssertionError("Expected a single row")

    assertThat(row.type).containsKnown(StringType.TYPE_A)
  }

  @Test
  fun `it roundtrips Unknown via text column`() = runWithRollback<MyTableDao> { dao ->
    val input = StringTableInput(WireSafeEnum.of("D"))

    val row = dao.insertString(input).let { dao.getString(it) }
      ?: throw AssertionError("Expected a single row")

    assertThat(row.type).containsUnknown("D")
  }

  @Test
  fun `it roundtrips Known via int column`() = runWithRollback<MyTableDao> { dao ->
    val input = IntTableInput(IntType.TYPE_A.wireSafe())

    val row = dao.insertInt(input).let { dao.getInt(it) }
      ?: throw AssertionError("Expected a single row")

    assertThat(row.type).containsKnown(IntType.TYPE_A)
  }

  @Test
  fun `it throws on Unknown insert into int column`() = runWithRollback<MyTableDao> { dao ->
    val input = IntTableInput(WireSafeEnum.of("D"))

    assertThatExceptionOfType(RuntimeException::class.java).isThrownBy {
      dao.insertInt(input)
    }
  }

  @Test
  fun `it throws on Unknown via int column`() = runWithRollback<MyTableDao> { dao ->


    assertThatExceptionOfType(RuntimeException::class.java).isThrownBy {
      dao.insertRawInteger(3).also { dao.getInt(it) }
    }
  }
}
