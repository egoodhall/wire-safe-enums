package com.egoodhall.wire.safe.enums.moshi

import com.egoodhall.ktools.wire.safe.enums.kotlinx.WireSafeEnum
import com.egoodhall.ktools.wire.safe.enums.kotlinx.wireSafe
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.assertj.core.api.ObjectAssert
import org.assertj.core.api.StringAssert
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MoshiTest {
  companion object {
    val MOSHI: Moshi =
      Moshi.Builder().add(WireSafeEnumAdapterFactory()).addLast(KotlinJsonAdapterFactory()).build()
    val KNOWN_VALUE: WireSafeEnum<TestEnum> = TestEnum.A.wireSafe()
    @OptIn(ExperimentalStdlibApi::class)
    val UNKNOWN_VALUE: WireSafeEnum<TestEnum> =
      MOSHI.adapter<WireSafeEnum<TestEnum>>().fromJson("\"B\"")!!
  }

  enum class TestEnum {
    A
  }

  data class TestWrapper(val field: WireSafeEnum<TestEnum>)

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

    @OptIn(ExperimentalStdlibApi::class)
    private inline fun <reified T> assertThatDeserializedJson(json: String): ObjectAssert<T> {
      val result = MOSHI.adapter<T>().fromJson(json)
      return ObjectAssert(result)
    }
  }

  @Nested
  inner class JsonSerialization {

    @Test
    fun `it serializes known value to JSON`() {
      assertThatSerializedJson(TestEnum.A.wireSafe())
        .isEqualTo(
          """
        "A"
      """
            .trimIndent()
        )
    }

    @Test
    fun `it serializes unknown value to JSON`() {
      assertThatSerializedJson(UNKNOWN_VALUE)
        .isEqualTo(
          """
        "B"
      """
            .trimIndent()
        )
    }

    @Test
    fun `it serializes known wrapped value to JSON`() {
      assertThatSerializedJson(TestWrapper(TestEnum.A.wireSafe()))
        .isEqualTo(
          """
          {"field":"A"}
        """
            .trimIndent()
        )
    }

    @Test
    fun `it serializes unknown wrapped value to JSON`() {
      assertThatSerializedJson(TestWrapper(UNKNOWN_VALUE))
        .isEqualTo(
          """
          {"field":"B"}
        """
            .trimIndent()
        )
    }

    @Test
    fun `it serializes known map value to JSON`() {
      assertThatSerializedJson(mapOf("field" to TestEnum.A.wireSafe()))
        .isEqualTo(
          """
          {"field":"A"}
        """
            .trimIndent()
        )
    }

    @Test
    fun `it serializes unknown map value to JSON`() {
      assertThatSerializedJson(mapOf("field" to UNKNOWN_VALUE))
        .isEqualTo(
          """
          {"field":"B"}
        """
            .trimIndent()
        )
    }

    @Test
    fun `it serializes known map key to JSON`() {
      assertThatSerializedJson(mapOf(TestEnum.A.wireSafe() to "field"))
        .isEqualTo(
          """
          {"A":"field"}
        """
            .trimIndent()
        )
    }

    @Test
    fun `it serializes unknown map key to JSON`() {
      assertThatSerializedJson(mapOf(UNKNOWN_VALUE to "field"))
        .isEqualTo(
          """
          {"B":"field"}
        """
            .trimIndent()
        )
    }

    @Test
    fun `it serializes known list element to JSON`() {
      assertThatSerializedJson(listOf(TestEnum.A.wireSafe()))
        .isEqualTo(
          """
          ["A"]
        """
            .trimIndent()
        )
    }

    @Test
    fun `it serializes unknown list element to JSON`() {
      assertThatSerializedJson(listOf(UNKNOWN_VALUE))
        .isEqualTo(
          """
          ["B"]
        """
            .trimIndent()
        )
    }

    @Test
    fun `it serializes known set element to JSON`() {
      assertThatSerializedJson(setOf(TestEnum.A.wireSafe()))
        .isEqualTo(
          """
          ["A"]
        """
            .trimIndent()
        )
    }

    @Test
    fun `it serializes unknown set element to JSON`() {
      assertThatSerializedJson(setOf(UNKNOWN_VALUE))
        .isEqualTo(
          """
          ["B"]
        """
            .trimIndent()
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    private inline fun <reified T> assertThatSerializedJson(pojo: T): StringAssert {
      val json = MOSHI.adapter<T>().toJson(pojo)
      return StringAssert(json)
    }
  }
}
