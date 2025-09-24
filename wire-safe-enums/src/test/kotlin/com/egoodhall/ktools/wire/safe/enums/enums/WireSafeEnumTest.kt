package com.egoodhall.ktools.wire.safe.enums.enums

import com.egoodhall.ktools.wire.safe.enums.WireSafeEnum
import com.egoodhall.ktools.wire.safe.enums.readWireSafeEnum
import com.egoodhall.ktools.wire.safe.enums.wireSafe
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.BooleanNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.InstanceOfAssertFactories.LIST
import org.assertj.core.api.InstanceOfAssertFactories.MAP
import org.assertj.core.api.ObjectAssert
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WireSafeEnumTest {
  companion object {
    val OBJECT_MAPPER = ObjectMapper().registerKotlinModule()
    val KNOWN_VALUE = TestEnum.A.wireSafe()
    val UNKNOWN_VALUE = OBJECT_MAPPER.readWireSafeEnum<TestEnum>("B")
  }

  enum class TestEnum {
    A
  }

  data class TestWrapper(val field: WireSafeEnum<TestEnum>)

  @Nested
  inner class JsonDeserialization {
    @Test
    fun `it deserializes from JSON`() {
      assertThatDeserializedJson<WireSafeEnum<TestEnum>>(
        """
    "A"      
  """
      ) {
        extracting { it.unwrap() }.isEqualTo(TestEnum.A)
        matches { it is WireSafeEnum.Known<TestEnum> }
      }
    }

    @Test
    fun `it handles unknown JSON values`() {
      assertThatDeserializedJson<WireSafeEnum<TestEnum>>(
        """
    "B"
  """
      ) {
        extracting { it.unwrap() }.isNull()
        matches { it is WireSafeEnum.Unknown<TestEnum> }
      }
    }

    @Test
    fun `it deserializes known value from wrapped JSON`() {
      assertThatDeserializedJson<TestWrapper>(
        """
      {
        "field": "A"
      }
    """
      ) {
        extracting { it.field.unwrap() }.isEqualTo(TestEnum.A)
        matches { it.field is WireSafeEnum.Known<TestEnum> }
      }
    }

    @Test
    fun `it deserializes unknown value from wrapped JSON`() {
      assertThatDeserializedJson<TestWrapper>(
        """
      {
        "field": "B"
      }
    """
      ) {
        extracting { it.field.unwrap() }.isNull()
        matches { it.field is WireSafeEnum.Unknown<TestEnum> }
      }
    }

    @Test
    fun `it deserializes known map key from JSON map`() {
      assertThatDeserializedJson<Map<WireSafeEnum<TestEnum>, Boolean>>(
        """
      {
        "A": true
      }
    """
      ) {
        asInstanceOf(MAP).containsEntry(KNOWN_VALUE, true)
      }
    }

    @Test
    fun `it deserializes unknown map key from JSON map`() {
      assertThatDeserializedJson<Map<WireSafeEnum<TestEnum>, Boolean>>(
        """
      {
        "B": true
      }
    """
      ) {
        asInstanceOf(MAP).containsEntry(UNKNOWN_VALUE, true)
      }
    }

    @Test
    fun `it deserializes known map value from JSON map`() {
      assertThatDeserializedJson<Map<String, WireSafeEnum<TestEnum>>>(
        """
      {
        "value": "A"
      }
    """
      ) {
        asInstanceOf(MAP).containsEntry("value", KNOWN_VALUE)
      }
    }

    @Test
    fun `it deserializes unknown map value from JSON map`() {
      assertThatDeserializedJson<Map<String, WireSafeEnum<TestEnum>>>(
        """
      {
        "value": "B"
      }
    """
      ) {
        asInstanceOf(MAP).containsEntry("value", UNKNOWN_VALUE)
      }
    }

    @Test
    fun `it deserializes known element from JSON array`() {
      assertThatDeserializedJson<List<WireSafeEnum<TestEnum>>>(
        """
        ["A"]
      """
      ) {
        asInstanceOf(LIST).containsExactly(KNOWN_VALUE)
      }
    }

    @Test
    fun `it deserializes unknown element from JSON array`() {
      assertThatDeserializedJson<List<WireSafeEnum<TestEnum>>>(
        """
        ["B"]
      """
      ) {
        asInstanceOf(LIST).containsExactly(UNKNOWN_VALUE)
      }
    }

    private inline fun <reified T> assertThatDeserializedJson(
      json: String,
      assertions: ObjectAssert<T>.() -> Unit,
    ) {
      val actual = OBJECT_MAPPER.readValue(json, object : TypeReference<T>() {})
      ObjectAssert(actual).assertions()
    }
  }

  @Nested
  inner class JsonSerialization {
    @Test
    fun `it serializes known value to JSON`() {
      assertThatSerializedJson(KNOWN_VALUE) { isEqualTo(TextNode.valueOf("A")) }
    }

    @Test
    fun `it serializes unknown value to JSON`() {
      assertThatSerializedJson(UNKNOWN_VALUE) { isEqualTo(TextNode.valueOf("B")) }
    }

    @Test
    fun `it serializes known wrapped value to JSON`() {
      assertThatSerializedJson(TestWrapper(KNOWN_VALUE)) {
        isEqualTo(
          OBJECT_MAPPER.nodeFactory.objectNode().apply {
            set<ObjectNode>("field", TextNode.valueOf("A"))
          }
        )
      }
    }

    @Test
    fun `it serializes unknown wrapped value to JSON`() {
      assertThatSerializedJson(TestWrapper(UNKNOWN_VALUE)) {
        isEqualTo(
          OBJECT_MAPPER.nodeFactory.objectNode().apply {
            set<ObjectNode>("field", TextNode.valueOf("B"))
          }
        )
      }
    }

    @Test
    fun `it serializes known map key to JSON`() {
      assertThatSerializedJson(mapOf(KNOWN_VALUE to true)) {
        isEqualTo(
          OBJECT_MAPPER.nodeFactory.objectNode().apply {
            set<ObjectNode>("A", BooleanNode.valueOf(true))
          }
        )
      }
    }

    @Test
    fun `it serializes unknown map key to JSON`() {
      assertThatSerializedJson(mapOf(UNKNOWN_VALUE to true)) {
        isEqualTo(
          OBJECT_MAPPER.nodeFactory.objectNode().apply {
            set<ObjectNode>("B", BooleanNode.valueOf(true))
          }
        )
      }
    }

    @Test
    fun `it serializes known map value to JSON`() {
      assertThatSerializedJson(mapOf("value" to KNOWN_VALUE)) {
        isEqualTo(
          OBJECT_MAPPER.nodeFactory.objectNode().apply {
            set<ObjectNode>("value", TextNode.valueOf("A"))
          }
        )
      }
    }

    @Test
    fun `it serializes unknown map value to JSON`() {
      assertThatSerializedJson(mapOf("value" to UNKNOWN_VALUE)) {
        isEqualTo(
          OBJECT_MAPPER.nodeFactory.objectNode().apply {
            set<ObjectNode>("value", TextNode.valueOf("B"))
          }
        )
      }
    }

    @Test
    fun `it serializes known list element to JSON`() {
      assertThatSerializedJson(listOf(KNOWN_VALUE)) {
        isEqualTo(OBJECT_MAPPER.nodeFactory.arrayNode().apply { add(TextNode.valueOf("A")) })
      }
    }

    @Test
    fun `it serializes unknown list element to JSON`() {
      assertThatSerializedJson(listOf(UNKNOWN_VALUE)) {
        isEqualTo(OBJECT_MAPPER.nodeFactory.arrayNode().apply { add(TextNode.valueOf("B")) })
      }
    }

    @Test
    fun `it serializes known set element to JSON`() {
      assertThatSerializedJson(setOf(KNOWN_VALUE)) {
        isEqualTo(OBJECT_MAPPER.nodeFactory.arrayNode().apply { add(TextNode.valueOf("A")) })
      }
    }

    @Test
    fun `it serializes unknown set element to JSON`() {
      assertThatSerializedJson(setOf(UNKNOWN_VALUE)) {
        isEqualTo(OBJECT_MAPPER.nodeFactory.arrayNode().apply { add(TextNode.valueOf("B")) })
      }
    }

    private inline fun <reified T> assertThatSerializedJson(
      pojo: T,
      assertions: ObjectAssert<JsonNode>.() -> Unit,
    ) {
      val json = OBJECT_MAPPER.convertValue(pojo, JsonNode::class.java)
      ObjectAssert(json).assertions()
    }
  }
}
