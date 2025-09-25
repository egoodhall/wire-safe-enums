package com.egoodhall.ktools.wire.safe.enums.enums

import com.egoodhall.ktools.wire.safe.enums.WireSafeEnum
import com.egoodhall.ktools.wire.safe.enums.decodeWireSafeEnum
import com.egoodhall.ktools.wire.safe.enums.wireSafe
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import org.assertj.core.api.ObjectAssert
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class KotlinxTest {
  companion object {
    val KNOWN_VALUE = TestEnum.A.wireSafe()
    val UNKNOWN_VALUE = Json.decodeWireSafeEnum<TestEnum>("\"B\"")
  }

  enum class TestEnum {
    A
  }

  @Serializable data class TestWrapper(val field: WireSafeEnum<TestEnum>)

  @Nested
  inner class JsonDeserialization {

    @Test
    fun `it deserializes known value from JSON`() {
      assertThatDeserializedJson<WireSafeEnum<TestEnum>>(
          """
        "A"
      """
        )
        .isEqualTo(KNOWN_VALUE)
    }

    @Test
    fun `it deserializes unknown value from JSON`() {
      assertThatDeserializedJson<WireSafeEnum<TestEnum>>(
          """
        "B"
      """
        )
        .isEqualTo(UNKNOWN_VALUE)
    }

    @Test
    fun `it deserializes known wrapped value from JSON`() {
      assertThatDeserializedJson<TestWrapper>(
          """
        {"field":"A"}
      """
        )
        .isEqualTo(TestWrapper(KNOWN_VALUE))
    }

    @Test
    fun `it deserializes unknown wrapped value from JSON`() {
      assertThatDeserializedJson<TestWrapper>(
          """
        {"field":"B"}
      """
        )
        .isEqualTo(TestWrapper(UNKNOWN_VALUE))
    }

    @Test
    fun `it deserializes known map value from JSON`() {
      assertThatDeserializedJson<Map<String, WireSafeEnum<TestEnum>>>(
          """
        {"field":"A"}
      """
        )
        .isEqualTo(mapOf("field" to KNOWN_VALUE))
    }

    @Test
    fun `it deserializes unknown map value from JSON`() {
      assertThatDeserializedJson<Map<String, WireSafeEnum<TestEnum>>>(
          """
        {"field":"B"}
      """
        )
        .isEqualTo(mapOf("field" to UNKNOWN_VALUE))
    }

    @Test
    fun `it deserializes known map key from JSON`() {
      assertThatDeserializedJson<Map<WireSafeEnum<TestEnum>, String>>(
          """
        {"A":"field"}
      """
        )
        .isEqualTo(mapOf(KNOWN_VALUE to "field"))
    }

    @Test
    fun `it deserializes unknown map key from JSON`() {
      assertThatDeserializedJson<Map<WireSafeEnum<TestEnum>, String>>(
          """
        {"B":"field"}
      """
        )
        .isEqualTo(mapOf(UNKNOWN_VALUE to "field"))
    }

    @Test
    fun `it deserializes known list element from JSON`() {
      assertThatDeserializedJson<List<WireSafeEnum<TestEnum>>>(
          """
        ["A"]
      """
        )
        .isEqualTo(listOf(KNOWN_VALUE))
    }

    @Test
    fun `it deserializes unknown list element from JSON`() {
      assertThatDeserializedJson<List<WireSafeEnum<TestEnum>>>(
          """
        ["B"]
      """
        )
        .isEqualTo(listOf(UNKNOWN_VALUE))
    }

    @Test
    fun `it deserializes known set element from JSON`() {
      assertThatDeserializedJson<Set<WireSafeEnum<TestEnum>>>(
          """
        ["A"]
      """
        )
        .isEqualTo(setOf(KNOWN_VALUE))
    }

    @Test
    fun `it deserializes unknown set element from JSON`() {
      assertThatDeserializedJson<Set<WireSafeEnum<TestEnum>>>(
          """
        ["B"]
      """
        )
        .isEqualTo(setOf(UNKNOWN_VALUE))
    }

    private inline fun <reified T> assertThatDeserializedJson(json: String): ObjectAssert<T> {
      val result = Json.decodeFromString<T>(json)
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
      val value = Json.decodeWireSafeEnum<TestEnum>("\"B\"")
      assertThatSerializedJson(value).isEqualTo(JsonPrimitive("B"))
    }

    @Test
    fun `it serializes known wrapped value to JSON`() {
      assertThatSerializedJson(TestWrapper(TestEnum.A.wireSafe()))
        .isEqualTo(JsonObject(mapOf("field" to JsonPrimitive("A"))))
    }

    @Test
    fun `it serializes unknown wrapped value to JSON`() {
      val value = Json.decodeWireSafeEnum<TestEnum>("\"B\"")
      assertThatSerializedJson(TestWrapper(value))
        .isEqualTo(JsonObject(mapOf("field" to JsonPrimitive("B"))))
    }

    @Test
    fun `it serializes known map value to JSON`() {
      assertThatSerializedJson(mapOf("field" to TestEnum.A.wireSafe()))
        .isEqualTo(JsonObject(mapOf("field" to JsonPrimitive("A"))))
    }

    @Test
    fun `it serializes unknown map value to JSON`() {
      val value = Json.decodeWireSafeEnum<TestEnum>("\"B\"")
      assertThatSerializedJson(mapOf("field" to value))
        .isEqualTo(JsonObject(mapOf("field" to JsonPrimitive("B"))))
    }

    @Test
    fun `it serializes known map key to JSON`() {
      assertThatSerializedJson(mapOf(TestEnum.A.wireSafe() to "field"))
        .isEqualTo(JsonObject(mapOf("A" to JsonPrimitive("field"))))
    }

    @Test
    fun `it serializes unknown map key to JSON`() {
      val value = Json.decodeWireSafeEnum<TestEnum>("\"B\"")
      assertThatSerializedJson(mapOf(value to "field"))
        .isEqualTo(JsonObject(mapOf("B" to JsonPrimitive("field"))))
    }

    @Test
    fun `it serializes known list element to JSON`() {
      assertThatSerializedJson(listOf(TestEnum.A.wireSafe()))
        .isEqualTo(JsonArray(listOf(JsonPrimitive("A"))))
    }

    @Test
    fun `it serializes unknown list element to JSON`() {
      val value = Json.decodeWireSafeEnum<TestEnum>("\"B\"")
      assertThatSerializedJson(listOf(value)).isEqualTo(JsonArray(listOf(JsonPrimitive("B"))))
    }

    @Test
    fun `it serializes known set element to JSON`() {
      assertThatSerializedJson(setOf(TestEnum.A.wireSafe()))
        .isEqualTo(JsonArray(listOf(JsonPrimitive("A"))))
    }

    @Test
    fun `it serializes unknown set element to JSON`() {
      val value = Json.decodeWireSafeEnum<TestEnum>("\"B\"")
      assertThatSerializedJson(setOf(value)).isEqualTo(JsonArray(listOf(JsonPrimitive("B"))))
    }

    private inline fun <reified T> assertThatSerializedJson(pojo: T): ObjectAssert<JsonElement> {
      val json = Json.encodeToJsonElement<T>(pojo)
      return ObjectAssert(json)
    }
  }
}
