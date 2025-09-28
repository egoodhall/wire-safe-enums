# ktools

Kotlin utilities

## Wire-safe enums

Wrapper class for enums that supports deserialization of unknown enum values. This is
especially useful when handling enums sent across the wire between different JVMs, where
an enum value may not be known.

### API

A `WireSafeEnum` can be either `Known` (the value is known
to the current JVM) or `Unknown` (the value is not known to the current JVM). When deserializing
an enum, if the string representation can be deserialized

```
```

### JSON Serialization

#### Jackson

Install `WireSafeEnumModule` into the `ObjectMapper` being used:

```kotlin
val objectMapper = ObjectMapper().apply {
  registerKotlinModule()
  registerModule(WireSafeEnumModule())
}
```

That will register the necessary `JsonSerializer`s and `JsonDeserializer`s to handle
transforming 