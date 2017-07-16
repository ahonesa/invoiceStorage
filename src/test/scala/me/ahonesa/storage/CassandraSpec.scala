package me.ahonesa.storage

import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl.ContactPoint
import org.scalatest.{BeforeAndAfterAll, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global

trait CassandraSpec extends WordSpec with BeforeAndAfterAll {
  val connector: CassandraConnection = ContactPoint.local.noHeartbeat().keySpace("test_db")
  object database extends InvoiceStorage(connector)

  override def beforeAll(): Unit = {
    database.truncate()
    database.create()
  }

  override def afterAll(): Unit = {
    database.connector.session.close()
  }
}
