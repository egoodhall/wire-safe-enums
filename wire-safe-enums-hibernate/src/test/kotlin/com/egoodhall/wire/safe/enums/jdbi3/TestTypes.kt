package com.egoodhall.wire.safe.enums.jdbi3

import com.egoodhall.tools.wire.safe.enums.WireSafeEnum
import jakarta.persistence.Column
import jakarta.persistence.Converter
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

enum class StringType {
  TYPE_A,
  TYPE_B,
}

@Converter(autoApply = true)
class StringTypeConverter : WireSafeEnumConverter<StringType>(StringType::class.java)

@Entity
@Table(name = "string_table")
open class StringTableRow() {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open var id: Int? = null

  @Column(nullable = false) open lateinit var type: WireSafeEnum<StringType>

  constructor(type: WireSafeEnum<StringType>) : this() {
    this.type = type
  }
}

enum class IntType {
  TYPE_A,
  TYPE_B,
}

@Converter(autoApply = true)
class IntTypeConverter : WireSafeEnumOrdinalConverter<IntType>(IntType::class.java)

@Entity
@Table(name = "int_table")
open class IntTableRow() {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) open var id: Int? = null

  @Column(nullable = false) open lateinit var type: WireSafeEnum<IntType>

  constructor(type: WireSafeEnum<IntType>) : this() {
    this.type = type
  }
}
