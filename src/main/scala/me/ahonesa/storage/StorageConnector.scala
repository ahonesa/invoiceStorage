package me.ahonesa.storage

import java.util.concurrent.TimeUnit

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints, KeySpace}
import com.outworkers.phantom.database.Database
import me.ahonesa.Main
import me.ahonesa.core.models.{Customer, NewCustomer}
import me.ahonesa.rest.utils.{Config, Logging}
import me.ahonesa.storage.db.{CustomerTable, InvoicesTable}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import com.outworkers.phantom.dsl._
import me.ahonesa.core.models.identifiers.CustomerId

import scala.util.{Failure, Success, Try}

object StorageConnector extends Config with Logging {
  lazy val connector: CassandraConnection = ContactPoints(hosts)
      .withClusterBuilder(_.withCredentials(username, password))
      .keySpace(keyspaceName)

  implicit val session = Try(connector.session) match {
    case Success(session) =>
      logger.info("Connected to Cassandra")
      session
    case Failure(ex) => {
      logger.error("Couldn't connect to Cassandra", ex)
      sys.exit(1)
    }
  }
}

class InvoiceStorage(override val connector: CassandraConnection)(executionContext: ExecutionContext)

  extends Database[InvoiceStorage](connector) with Config with Logging {

  object CustomerTable extends CustomerTable with connector.Connector
  object InvoicesTable extends InvoicesTable with connector.Connector

  implicit val keySpace = KeySpace(keyspaceName)

  Try{
    Await.ready(CustomerTable.create.ifNotExists().future(), 10.seconds)
    Await.ready(InvoicesTable.create.ifNotExists().future(), 10.seconds)
  } match {
    case Success(_) => Unit
    case Failure(ex) => {
      logger.error("Exception occurred when creating Cassandra tables", ex)
      sys.exit(1)
    }
  }

  def findByCustomerId(id: CustomerId): Future[Option[Customer]] = {
    CustomerTable.findByCustomerId(id)
  }

  def createCustomer(id: CustomerId, newCustomer: NewCustomer): Future[Option[Customer]] = {
    val customer = Customer(id, newCustomer.name.getOrElse(""))
    CustomerTable.createCustomer(customer.id, customer.name).map( resultSet =>
      if(resultSet.wasApplied()) Some(customer) else None
    )
  }
}


