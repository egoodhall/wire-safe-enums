package com.egoodhall.wire.safe.enums.jdbi3

import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

abstract class WireSafeEnumHibernateTest {

  protected fun <T> runWithRollback(block: (org.hibernate.Session) -> T): Unit =
    sessionFactory.openSession().use { session ->
      val tx = session.beginTransaction()
      try {
        block(session)
      } finally {
        tx.rollback()
      }
    }

  companion object {
    private lateinit var sessionFactory: SessionFactory

    @JvmStatic
    @BeforeAll
    fun setUp() {
      sessionFactory =
        Configuration()
          .setProperty("hibernate.connection.url", "jdbc:sqlite::memory:")
          .setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC")
          .setProperty("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect")
          .setProperty("hibernate.hbm2ddl.auto", "create-drop")
          .setProperty("hibernate.show_sql", "false")
          .addAnnotatedClass(StringTableRow::class.java)
          .addAnnotatedClass(IntTableRow::class.java)
          .addAttributeConverter(StringTypeConverter::class.java)
          .addAttributeConverter(IntTypeConverter::class.java)
          .buildSessionFactory()
    }

    @JvmStatic
    @AfterAll
    fun tearDown() {
      if (::sessionFactory.isInitialized) sessionFactory.close()
    }
  }
}
