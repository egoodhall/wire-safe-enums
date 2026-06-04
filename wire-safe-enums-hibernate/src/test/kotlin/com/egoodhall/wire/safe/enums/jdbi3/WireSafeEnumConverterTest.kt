package com.egoodhall.wire.safe.enums.jdbi3

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import com.egoodhall.tools.wire.safe.enums.assertj.assertThat
import com.egoodhall.tools.wire.safe.enums.wireSafe
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test

class WireSafeEnumConverterTest : WireSafeEnumHibernateTest() {

  @Test
  fun `it roundtrips Known via text column`() = runWithRollback { session ->
    val saved = StringTableRow(StringType.TYPE_A.wireSafe()).also { session.persist(it) }
    session.flush()
    session.clear()

    val loaded = session.find(StringTableRow::class.java, saved.id)
      ?: throw AssertionError("Expected a single row")

    assertThat(loaded.type).containsKnown(StringType.TYPE_A)
  }

  @Test
  fun `it roundtrips Unknown via text column`() = runWithRollback { session ->
    val saved = StringTableRow(WireSafeEnum.of("D")).also { session.persist(it) }
    session.flush()
    session.clear()

    val loaded = session.find(StringTableRow::class.java, saved.id)
      ?: throw AssertionError("Expected a single row")

    assertThat(loaded.type).containsUnknown("D")
  }

  @Test
  fun `it roundtrips Known via int column`() = runWithRollback { session ->
    val saved = IntTableRow(IntType.TYPE_A.wireSafe()).also { session.persist(it) }
    session.flush()
    session.clear()

    val loaded = session.find(IntTableRow::class.java, saved.id)
      ?: throw AssertionError("Expected a single row")

    assertThat(loaded.type).containsKnown(IntType.TYPE_A)
  }

  @Test
  fun `it throws when reading unmapped ordinal from int column`() = runWithRollback { session ->
    session.createNativeMutationQuery("INSERT INTO int_table (type) VALUES (9)").executeUpdate()
    session.flush()
    session.clear()

    assertThatExceptionOfType(RuntimeException::class.java).isThrownBy {
      session.createQuery("FROM IntTableRow", IntTableRow::class.java).resultList
    }
  }

  @Test
  fun `it throws when persisting Unknown to int column`() = runWithRollback { session ->
    assertThatExceptionOfType(RuntimeException::class.java).isThrownBy {
      session.persist(IntTableRow(WireSafeEnum.of("anything")))
      session.flush()
    }
  }
}
