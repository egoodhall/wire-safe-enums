package com.egoodhall.wire.safe.enums.jdbi3

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import java.lang.reflect.Type
import java.sql.JDBCType
import java.sql.JDBCType.VARCHAR
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.result.UnableToProduceResultException
import org.jdbi.v3.core.statement.StatementContext

class WireSafeEnumColumnMapper<T : Enum<T>>(private val type: Type) :
  ColumnMapper<WireSafeEnum<T>> {
  override fun map(result: ResultSet, columnNumber: Int, ctx: StatementContext): WireSafeEnum<T> {
    val delegate: ColumnMapper<T> = ctx.findColumnMapperFor(type).orElseThrow() as ColumnMapper<T>
    try {
      return WireSafeEnum.of(delegate.map(result, columnNumber, ctx))
    } catch (e: UnableToProduceResultException) {
      return when (val columnType = JDBCType.valueOf(result.metaData.getColumnType(columnNumber))) {
        VARCHAR -> WireSafeEnum.of(result.getString(columnNumber))
        else -> throw UnableToProduceResultException("Can't determine unknown type for value: $columnType")
      }
    }
  }
}
