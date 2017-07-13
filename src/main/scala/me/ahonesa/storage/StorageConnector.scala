package me.ahonesa.storage

import java.util.concurrent.TimeUnit

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints, KeySpace}
import com.outworkers.phantom.database.Database
import me.ahonesa.Main
import me.ahonesa.core.models.{Customer, NewCustomer}
import me.ahonesa.rest.utils.Config
import me.ahonesa.storage.db.CustomerTable

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

object StorageConnector extends Config {
  lazy val connector: CassandraConnection = ContactPoints(hosts)
      .withClusterBuilder(_.withCredentials(username, password))
      .keySpace(keyspaceName)
}


class InvoiceStorage(override val connector: CassandraConnection)(executionContext: ExecutionContext)
  extends Database[InvoiceStorage](connector) with Config {

  implicit val keySpace = KeySpace(keyspaceName)
  implicit val executor = executionContext.

  object CustomerTable extends CustomerTable with connector.Connector

  def createTable = CustomerTable.create.ifNotExists().future()

  def findByCustomerId(id: String): Future[Option[Customer]] = {
    CustomerTable.findByCustomerId(id)
  }

  def createCustomer(id: String, newCustomer: NewCustomer): Future[Option[Customer]] = {
    val customer = Customer(id, newCustomer.name.getOrElse(""))
    CustomerTable.createCustomer(customer.id, customer.name).map( resultSet =>
      if(resultSet.wasApplied()) Some(customer) else None
    )
  }
}


object Database extends InvoiceStorage(StorageConnector.connector)(Main.executor)
