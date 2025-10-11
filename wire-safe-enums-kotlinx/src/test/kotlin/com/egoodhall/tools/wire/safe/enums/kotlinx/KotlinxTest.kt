package com.egoodhall.tools.wire.safe.enums.kotlinx

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import com.egoodhall.tools.wire.safe.enums.wireSafe
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import org.assertj.core.api.ObjectAssert
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class KotlinxTest {
  companion object {
    val JSON = Json { serializersModule = WireSafeEnumModule }
    val KNOWN_VALUE: WireSafeEnum<TestEnum> = TestEnum.A.wireSafe()
    val UNKNOWN_VALUE: WireSafeEnum<TestEnum> = WireSafeEnum.of("B")
  }

  enum class TestEnum {
    A
  }

  @Serializable data class TestWrapper(@Contextual val field: WireSafeEnum<TestEnum>)

  @Nested
  inner class JsonDeserialization {

    @Test
    fun `it deserializes known value from JSON`() {
      assertThatDeserializedJson<WireSafeEnum<TestEnum>>("\"A\"").isEqualTo(KNOWN_VALUE)
    }

    @Test
    fun `it deserializes unknown value from JSON`() {
      assertThatDeserializedJson<WireSafeEnum<TestEnum>>("\"B\"").isEqualTo(UNKNOWN_VALUE)
    }

    @Test
    fun `it deserializes known wrapped value from JSON`() {
      assertThatDeserializedJson<TestWrapper>("{\"field\":\"A\"}")
        .isEqualTo(TestWrapper(KNOWN_VALUE))
    }

    @Test
    fun `it deserializes unknown wrapped value from JSON`() {
      assertThatDeserializedJson<TestWrapper>("{\"field\":\"B\"}")
        .isEqualTo(TestWrapper(UNKNOWN_VALUE))
    }

    @Test
    fun `it deserializes known map value from JSON`() {
      assertThatDeserializedJson<Map<String, WireSafeEnum<TestEnum>>>("{\"field\":\"A\"}")
        .isEqualTo(mapOf("field" to KNOWN_VALUE))
    }

    @Test
    fun `it deserializes unknown map value from JSON`() {
      assertThatDeserializedJson<Map<String, WireSafeEnum<TestEnum>>>("{\"field\":\"B\"}")
        .isEqualTo(mapOf("field" to UNKNOWN_VALUE))
    }

    @Test
    fun `it deserializes known map key from JSON`() {
      assertThatDeserializedJson<Map<WireSafeEnum<TestEnum>, String>>("{\"A\":\"field\"}")
        .isEqualTo(mapOf(KNOWN_VALUE to "field"))
    }

    @Test
    fun `it deserializes unknown map key from JSON`() {
      assertThatDeserializedJson<Map<WireSafeEnum<TestEnum>, String>>("{\"B\":\"field\"}")
        .isEqualTo(mapOf(UNKNOWN_VALUE to "field"))
    }

    @Test
    fun `it deserializes known list element from JSON`() {
      assertThatDeserializedJson<List<WireSafeEnum<TestEnum>>>("[\"A\"]")
        .isEqualTo(listOf(KNOWN_VALUE))
    }

    @Test
    fun `it deserializes unknown list element from JSON`() {
      assertThatDeserializedJson<List<WireSafeEnum<TestEnum>>>("[\"B\"]")
        .isEqualTo(listOf(UNKNOWN_VALUE))
    }

    @Test
    fun `it deserializes known set element from JSON`() {
      assertThatDeserializedJson<Set<WireSafeEnum<TestEnum>>>("[\"A\"]")
        .isEqualTo(setOf(KNOWN_VALUE))
    }

    @Test
    fun `it deserializes unknown set element from JSON`() {
      assertThatDeserializedJson<Set<WireSafeEnum<TestEnum>>>("[\"B\"]")
        .isEqualTo(setOf(UNKNOWN_VALUE))
    }

    private inline fun <reified T> assertThatDeserializedJson(json: String): ObjectAssert<T> {
      val result = JSON.decodeFromString<T>(json)
      return ObjectAssert(result)
    }
  }

  @Nested
  inner class JsonSerialization {

    @Test
    fun `it serializes known value to JSON`() {
      assertThatSerializedJson(TestEnum.A.wireSafe()).isEqualTo(JsonPrimitive("A"))
    }

    @Test
    fun `it serializes unknown value to JSON`() {
      assertThatSerializedJson(UNKNOWN_VALUE).isEqualTo(JsonPrimitive("B"))
    }

    @Test
    fun `it serializes known wrapped value to JSON`() {
      assertThatSerializedJson(TestWrapper(TestEnum.A.wireSafe()))
        .isEqualTo(JsonObject(mapOf("field" to JsonPrimitive("A"))))
    }

    @Test
    fun `it serializes unknown wrapped value to JSON`() {
      assertThatSerializedJson(TestWrapper(UNKNOWN_VALUE))
        .isEqualTo(JsonObject(mapOf("field" to JsonPrimitive("B"))))
    }

    @Test
    fun `it serializes known map value to JSON`() {
      assertThatSerializedJson(mapOf("field" to TestEnum.A.wireSafe()))
        .isEqualTo(JsonObject(mapOf("field" to JsonPrimitive("A"))))
    }

    @Test
    fun `it serializes unknown map value to JSON`() {
      assertThatSerializedJson(mapOf("field" to UNKNOWN_VALUE))
        .isEqualTo(JsonObject(mapOf("field" to JsonPrimitive("B"))))
    }

    @Test
    fun `it serializes known map key to JSON`() {
      assertThatSerializedJson(mapOf(TestEnum.A.wireSafe() to "field"))
        .isEqualTo(JsonObject(mapOf("A" to JsonPrimitive("field"))))
    }

    @Test
    fun `it serializes unknown map key to JSON`() {
      assertThatSerializedJson(mapOf(UNKNOWN_VALUE to "field"))
        .isEqualTo(JsonObject(mapOf("B" to JsonPrimitive("field"))))
    }

    @Test
    fun `it serializes known list element to JSON`() {
      assertThatSerializedJson(listOf(TestEnum.A.wireSafe()))
        .isEqualTo(JsonArray(listOf(JsonPrimitive("A"))))
    }

    @Test
    fun `it serializes unknown list element to JSON`() {
      assertThatSerializedJson(listOf(UNKNOWN_VALUE))
        .isEqualTo(JsonArray(listOf(JsonPrimitive("B"))))
    }

    @Test
    fun `it serializes known set element to JSON`() {
      assertThatSerializedJson(setOf(TestEnum.A.wireSafe()))
        .isEqualTo(JsonArray(listOf(JsonPrimitive("A"))))
    }

    @Test
    fun `it serializes unknown set element to JSON`() {
      assertThatSerializedJson(setOf(UNKNOWN_VALUE))
        .isEqualTo(JsonArray(listOf(JsonPrimitive("B"))))
    }

    private inline fun <reified T> assertThatSerializedJson(pojo: T): ObjectAssert<JsonElement> {
      val json = JSON.encodeToJsonElement<T>(pojo)
      return ObjectAssert(json)
    }
  }
}
