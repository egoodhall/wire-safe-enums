package com.egoodhall.wire.safe.enums.jdbi3

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import java.lang.reflect.Type
import java.util.Optional
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.config.ConfigRegistry
import org.jdbi.v3.core.generic.GenericTypes.findGenericParameter
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.mapper.ColumnMapperFactory
import org.jdbi.v3.core.spi.JdbiPlugin

class WireSafeEnumPlugin : JdbiPlugin {

  override fun customizeJdbi(jdbi: Jdbi) {
    jdbi.registerColumnMapper(WireSafeEnumColumnMapperFactory())
    jdbi.registerArgument(WireSafeEnumArgumentFactory())
  }
}

private class WireSafeEnumColumnMapperFactory : ColumnMapperFactory {
  override fun build(type: Type, config: ConfigRegistry): Optional<ColumnMapper<*>> {
    if (WireSafeEnum.isWireSafeEnum(type)) {
      return findGenericParameter(type, WireSafeEnum::class.java).map { WireSafeEnumColumnMapper(it) }
    }
    return Optional.empty()
  }
}
