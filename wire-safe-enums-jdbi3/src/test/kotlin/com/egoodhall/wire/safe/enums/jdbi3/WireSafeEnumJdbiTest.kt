package com.egoodhall.wire.safe.enums.jdbi3

import liquibase.Contexts
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.jdbi.v3.testing.junit5.JdbiExtension
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.RegisterExtension

abstract class WireSafeEnumJdbiTest {
  protected inline fun <reified T> runWithRollback(crossinline block: (T) -> Unit) {
    sqlite.jdbi.useTransaction<RuntimeException> { tx ->
      try {
        block(tx.attach(T::class.java))
      } finally {
        tx.rollback()
      }
    }
  }

  companion object {
    @JvmStatic
    @RegisterExtension
    val sqlite: JdbiExtension =
      JdbiExtension.sqlite()
        .withPlugin(KotlinPlugin())
        .withPlugin(KotlinSqlObjectPlugin())
        .withPlugin(WireSafeEnumPlugin())

    @JvmStatic
    @BeforeAll
    fun setUp() =
      sqlite.jdbi.useHandle<RuntimeException> { handle ->
        val db =
          DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(JdbcConnection(handle.connection))
        Liquibase("migrations.sql", ClassLoaderResourceAccessor(), db).use {
          it.update(Contexts("job"))
        }
      }
  }
}
