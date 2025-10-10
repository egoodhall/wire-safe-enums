package com.egoodhall.tools.wire.safe.enums.jackson

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import com.egoodhall.tools.wire.safe.enums.assertj.WireSafeEnumAssert
import com.egoodhall.tools.wire.safe.enums.wireSafe
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.InstanceOfAssertFactories
import org.assertj.core.api.ObjectAssert
import org.assertj.core.api.StringAssert
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Jackson2Test {
  companion object {
    val OBJECT_MAPPER =
      ObjectMapper().apply {
        registerKotlinModule()
        registerModule(WireSafeEnumModule())
      }
    val KNOWN_VALUE: WireSafeEnum<TestEnum> = TestEnum.A.wireSafe()
    val UNKNOWN_VALUE: WireSafeEnum<TestEnum> = WireSafeEnum.Companion.of("B")
  }

  enum class TestEnum {
    A
  }

  data class TestWrapper(val field: WireSafeEnum<TestEnum>)

  @Nested
  inner class JsonDeserialization {
    @Test
    fun `it deserializes from JSON`() {
      assertThatDeserializedJson<WireSafeEnum<TestEnum>>("\"A\"")
        .asInstanceOf(WireSafeEnumAssert.Companion.factory<TestEnum>())
        .isEqualTo(KNOWN_VALUE)
    }

    @Test
    fun `it handles unknown JSON values`() {
      assertThatDeserializedJson<WireSafeEnum<TestEnum>>("\"B\"")
        .asInstanceOf(WireSafeEnumAssert.Companion.factory<TestEnum>())
        .isEqualTo(UNKNOWN_VALUE)
    }

    @Test
    fun `it deserializes known value from wrapped JSON`() {
      assertThatDeserializedJson<TestWrapper>("{ \"field\": \"A\" }")
        .extracting { it.field }
        .asInstanceOf(WireSafeEnumAssert.Companion.factory<TestEnum>())
        .isEqualTo(KNOWN_VALUE)
    }

    @Test
    fun `it deserializes unknown value from wrapped JSON`() {
      assertThatDeserializedJson<TestWrapper>("{ \"field\": \"B\" }")
        .extracting { it.field }
        .asInstanceOf(WireSafeEnumAssert.Companion.factory<TestEnum>())
        .isEqualTo(UNKNOWN_VALUE)
    }

    @Test
    fun `it deserializes known map key from JSON map`() {
      assertThatDeserializedJson<Map<WireSafeEnum<TestEnum>, Boolean>>("{\"A\":true}")
        .asInstanceOf(InstanceOfAssertFactories.MAP)
        .containsKey(KNOWN_VALUE)
    }

    @Test
    fun `it deserializes unknown map key from JSON map`() {
      assertThatDeserializedJson<Map<WireSafeEnum<TestEnum>, Boolean>>("{\"B\":true}")
        .asInstanceOf(InstanceOfAssertFactories.MAP)
        .containsKey(UNKNOWN_VALUE)
    }

    @Test
    fun `it deserializes known map value from JSON map`() {
      assertThatDeserializedJson<Map<String, WireSafeEnum<TestEnum>>>("{\"field\":\"A\"}")
        .asInstanceOf(InstanceOfAssertFactories.MAP)
        .extractingByKey("field")
        .asInstanceOf(WireSafeEnumAssert.Companion.factory<TestEnum>())
        .isEqualTo(KNOWN_VALUE)
    }

    @Test
    fun `it deserializes unknown map value from JSON map`() {
      assertThatDeserializedJson<Map<String, WireSafeEnum<TestEnum>>>("{\"field\":\"B\"}")
        .asInstanceOf(InstanceOfAssertFactories.MAP)
        .extractingByKey("field")
        .asInstanceOf(WireSafeEnumAssert.Companion.factory<TestEnum>())
        .isEqualTo(UNKNOWN_VALUE)
    }

    @Test
    fun `it deserializes known element from JSON array`() {
      assertThatDeserializedJson<List<WireSafeEnum<TestEnum>>>("[\"A\"]")
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .first()
        .asInstanceOf(WireSafeEnumAssert.Companion.factory<TestEnum>())
        .isEqualTo(KNOWN_VALUE)
    }

    @Test
    fun `it deserializes unknown element from JSON array`() {
      assertThatDeserializedJson<List<WireSafeEnum<TestEnum>>>("[\"B\"]")
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .first()
        .asInstanceOf(WireSafeEnumAssert.Companion.factory<TestEnum>())
        .isEqualTo(UNKNOWN_VALUE)
    }

    private inline fun <reified T> assertThatDeserializedJson(json: String): ObjectAssert<T> {
      val actual = OBJECT_MAPPER.readValue(json, object : TypeReference<T>() {})
      return ObjectAssert(actual)
    }
  }

  @Nested
  inner class JsonSerialization {
    @Test
    fun `it serializes known value to JSON`() {
      assertThatSerializedJson(KNOWN_VALUE).isEqualTo("\"A\"")
    }

    @Test
    fun `it serializes unknown value to JSON`() {
      assertThatSerializedJson(UNKNOWN_VALUE).isEqualTo("\"B\"")
    }

    @Test
    fun `it serializes known wrapped value to JSON`() {
      assertThatSerializedJson(TestWrapper(KNOWN_VALUE)).isEqualTo("{\"field\":\"A\"}")
    }

    @Test
    fun `it serializes unknown wrapped value to JSON`() {
      assertThatSerializedJson(TestWrapper(UNKNOWN_VALUE)).isEqualTo("{\"field\":\"B\"}")
    }

    @Test
    fun `it serializes known map key to JSON`() {
      assertThatSerializedJson(mapOf(KNOWN_VALUE to true)).isEqualTo("{\"A\":true}")
    }

    @Test
    fun `it serializes unknown map key to JSON`() {
      assertThatSerializedJson(mapOf(UNKNOWN_VALUE to true)).isEqualTo("{\"B\":true}")
    }

    @Test
    fun `it serializes known map value to JSON`() {
      assertThatSerializedJson(mapOf("field" to KNOWN_VALUE)).isEqualTo("{\"field\":\"A\"}")
    }

    @Test
    fun `it serializes unknown map value to JSON`() {
      assertThatSerializedJson(mapOf("field" to UNKNOWN_VALUE)).isEqualTo("{\"field\":\"B\"}")
    }

    @Test
    fun `it serializes known list element to JSON`() {
      assertThatSerializedJson(listOf(KNOWN_VALUE)).isEqualTo("[\"A\"]")
    }

    @Test
    fun `it serializes unknown list element to JSON`() {
      assertThatSerializedJson(listOf(UNKNOWN_VALUE)).isEqualTo("[\"B\"]")
    }

    @Test
    fun `it serializes known set element to JSON`() {
      assertThatSerializedJson(setOf(KNOWN_VALUE)).isEqualTo("[\"A\"]")
    }

    @Test
    fun `it serializes unknown set element to JSON`() {
      assertThatSerializedJson(setOf(UNKNOWN_VALUE)).isEqualTo("[\"B\"]")
    }

    private inline fun <reified T> assertThatSerializedJson(pojo: T): StringAssert {
      val json = OBJECT_MAPPER.writeValueAsString(pojo)
      return StringAssert(json)
    }
  }
}
