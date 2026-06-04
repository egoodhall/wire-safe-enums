package com.egoodhall.wire.safe.enums.jdbi3

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import org.jdbi.v3.core.enums.EnumByOrdinal
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

data class StringTableRow(val id: Int, val type: WireSafeEnum<StringType>)

data class StringTableInput(val type: WireSafeEnum<StringType>)

enum class StringType {
  TYPE_A,
  TYPE_B,
}

data class IntTableRow(val id: Int, val type: WireSafeEnum<IntType>)

data class IntTableInput(val type: WireSafeEnum<IntType>)

@EnumByOrdinal
enum class IntType(val id: Int) {
  TYPE_A(1),
  TYPE_B(2),
}

interface MyTableDao {

  @SqlUpdate(
    """
    INSERT INTO string_table (type) VALUES (:input.type)
  """
  )
  @GetGeneratedKeys
  fun insertString(input: StringTableInput): Int

  @SqlQuery(
    """
    SELECT * FROM string_table WHERE id = :id LIMIT 1
  """
  )
  fun getString(id: Int): StringTableRow?

  @SqlUpdate(
    """
    INSERT INTO int_table (type) VALUES (:type)
  """
  )
  @GetGeneratedKeys
  fun insertRawInteger(type: Int): Int

  @SqlUpdate(
    """
    INSERT INTO int_table (type) VALUES (:input.type)
  """
  )
  @GetGeneratedKeys
  fun insertInt(input: IntTableInput): Int

  @SqlQuery(
    """
    SELECT * FROM int_table WHERE id = :id LIMIT 1
  """
  )
  fun getInt(id: Int): IntTableRow?
}
