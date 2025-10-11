package com.egoodhall.tools.wire.safe.enums.jackson

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import com.fasterxml.jackson.annotation.JsonFormat
import tools.jackson.databind.BeanDescription
import tools.jackson.databind.DeserializationConfig
import tools.jackson.databind.JavaType
import tools.jackson.databind.KeyDeserializer
import tools.jackson.databind.SerializationConfig
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.deser.Deserializers
import tools.jackson.databind.deser.KeyDeserializers
import tools.jackson.databind.module.SimpleModule
import tools.jackson.databind.ser.Serializers

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
    if (WireSafeEnum.isWireSafeEnum(type.rawClass)) {
      supplier()
    } else {
      null
    }
}

internal class WireSafeEnumSerializers : Serializers.Base(), WireSafeEnumJacksonUtil {
  override fun findSerializer(
    config: SerializationConfig?,
    type: JavaType,
    beanDescRef: BeanDescription.Supplier?,
    formatOverrides: JsonFormat.Value?,
  ): ValueSerializer<*>? {
    return whenWireSafeEnum(type) { WireSafeEnumSerializer() }
  }
}

internal class WireSafeEnumKeySerializers : Serializers.Base(), WireSafeEnumJacksonUtil {
  override fun findSerializer(
    config: SerializationConfig?,
    type: JavaType,
    beanDescRef: BeanDescription.Supplier?,
    formatOverrides: JsonFormat.Value?,
  ): ValueSerializer<*>? {
    return whenWireSafeEnum(type) { WireSafeEnumKeySerializer() }
  }
}

internal class WireSafeEnumDeserializers : Deserializers.Base(), WireSafeEnumJacksonUtil {

  override fun findBeanDeserializer(
    type: JavaType,
    config: DeserializationConfig?,
    beanDescRef: BeanDescription.Supplier?,
  ): ValueDeserializer<*>? {
    return whenWireSafeEnum(type) { WireSafeEnumDeserializer() }
  }

  override fun hasDeserializerFor(config: DeserializationConfig?, valueType: Class<*>): Boolean {
    return WireSafeEnum.isWireSafeEnum(valueType)
  }
}

internal class WireSafeEnumKeyDeserializers : KeyDeserializers, WireSafeEnumJacksonUtil {
  override fun findKeyDeserializer(
    type: JavaType,
    config: DeserializationConfig?,
    beanDescRef: BeanDescription.Supplier?,
  ): KeyDeserializer? {
    return whenWireSafeEnum(type) { WireSafeEnumKeyDeserializer() }
  }
}
