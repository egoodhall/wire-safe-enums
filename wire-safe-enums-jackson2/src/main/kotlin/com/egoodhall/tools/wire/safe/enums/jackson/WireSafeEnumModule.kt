package com.egoodhall.tools.wire.safe.enums.jackson

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.deser.KeyDeserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.Serializers

class WireSafeEnumModule : SimpleModule("wire-safe-enum") {
  override fun setupModule(context: SetupContext) {
    context.apply {
      addSerializers(WireSafeEnumSerializers())
      addKeySerializers(WireSafeEnumKeySerializers())
      addDeserializers(WireSafeEnumDeserializers())
      addKeyDeserializers(WireSafeEnumKeyDeserializers())
    }
  }
}

internal interface WireSafeEnumJacksonUtil {
  fun <T> whenWireSafeEnum(type: JavaType, supplier: () -> T): T? =
    if (WireSafeEnum.Companion.isWireSafeEnum(type.rawClass)) {
      supplier()
    } else {
      null
    }
}

internal class WireSafeEnumSerializers : Serializers.Base(), WireSafeEnumJacksonUtil {
  override fun findSerializer(
    config: SerializationConfig,
    type: JavaType,
    beanDesc: BeanDescription?,
  ): JsonSerializer<*>? {
    return whenWireSafeEnum(type) { WireSafeEnumSerializer() }
  }
}

internal class WireSafeEnumKeySerializers : Serializers.Base(), WireSafeEnumJacksonUtil {
  override fun findSerializer(
    config: SerializationConfig,
    type: JavaType,
    beanDesc: BeanDescription?,
  ): JsonSerializer<*>? {
    return whenWireSafeEnum(type) { WireSafeEnumKeySerializer() }
  }
}

internal class WireSafeEnumDeserializers : Deserializers.Base(), WireSafeEnumJacksonUtil {
  override fun findBeanDeserializer(
    type: JavaType,
    config: DeserializationConfig,
    beanDesc: BeanDescription?,
  ): JsonDeserializer<*>? {
    return whenWireSafeEnum(type) { WireSafeEnumDeserializer() }
  }
}

internal class WireSafeEnumKeyDeserializers : KeyDeserializers, WireSafeEnumJacksonUtil {
  override fun findKeyDeserializer(
    type: JavaType,
    config: DeserializationConfig?,
    beanDesc: BeanDescription?,
  ): KeyDeserializer? {
    return whenWireSafeEnum(type) { WireSafeEnumKeyDeserializer() }
  }
}
