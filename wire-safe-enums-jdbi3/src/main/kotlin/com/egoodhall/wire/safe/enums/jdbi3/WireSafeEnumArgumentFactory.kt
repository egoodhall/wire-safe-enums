package com.egoodhall.wire.safe.enums.jdbi3

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import java.lang.reflect.Type
import java.util.Optional
import org.jdbi.v3.core.argument.Argument
import org.jdbi.v3.core.argument.ArgumentFactory
import org.jdbi.v3.core.argument.Arguments
import org.jdbi.v3.core.config.ConfigRegistry

class WireSafeEnumArgumentFactory : ArgumentFactory {
  override fun build(expectedType: Type, value: Any, config: ConfigRegistry): Optional<Argument> {
    if (value is WireSafeEnum<*>) {
      val arguments = config.get(Arguments::class.java)
      return value.match(
        known = { handleKnown(arguments, it) },
        unknown = { handleUnknown(arguments, expectedType, it) }
      )
    }
    return Optional.empty()
  }

  private fun handleKnown(args: Arguments, value: Any): Optional<Argument> {
    return args.findFor(value.javaClass, value)
  }

  private fun handleUnknown(args: Arguments, expectedType: Type, value: String): Optional<Argument> {
    return args.findFor(expectedType, value)
  }
}
