package me.ahonesa.storage

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints}
import com.outworkers.phantom.database.Database
import me.ahonesa.core.models._
import me.ahonesa.rest.utils.{Config}
import me.ahonesa.storage.db.{CustomerTable, InvoicesTable}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import com.outworkers.phantom.dsl._
import com.typesafe.scalalogging.LazyLogging
import me.ahonesa.core.models.identifiers._

import scala.util.{Failure, Success, Try}

object StorageConnector extends Config with LazyLogging {
  lazy val connector: CassandraConnection = ContactPoints(hosts)
      .withClusterBuilder(_.withCredentials(username, password))
      .keySpace(keyspaceName)

  // Test connection
  Try(connector.session) match {
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
  extends Database[InvoiceStorage](connector) with Config with LazyLogging with RootConnector {

  object CustomerTable extends CustomerTable with connector.Connector
  object InvoicesTable extends InvoicesTable with connector.Connector

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

  def findCustomerById(id: CustomerId): Future[Option[Customer]] = {
    CustomerTable.findById(id)
  }

  def findInvoiceById(id: InvoiceId): Future[Option[Invoice]] = {
    InvoicesTable.findById(id)
  }

  def findInvoicesByCustomerId(id: CustomerId): Future[List[Invoice]] = {
    InvoicesTable.findByCustomerId(id)
  }

  def createCustomer(id: CustomerId, customerDetails: CustomerDetails): Future[Option[Customer]] = {
    val customer = Customer(id, customerDetails)
    CustomerTable.store(customer).future().map( resultSet =>
      if(resultSet.wasApplied()) Some(customer) else None
    )
  }

  def createInvoice(id: InvoiceId, newInvoice: NewInvoice): Future[Option[Invoice]] = {
    val invoice = Invoice(id, newInvoice.customerId, newInvoice.invoiceDate, newInvoice.invoiceSummary, newInvoice.invoiceStatus, Set())
    InvoicesTable.store(invoice).future().map( resultSet =>
      if(resultSet.wasApplied()) Some(invoice) else None
    )
  }

  def updateInvoice(invoice: Invoice): Future[Option[Invoice]] = {
    InvoicesTable.store(invoice).future().map( resultSet =>
      if(resultSet.wasApplied()) Some(invoice) else None
    )
  }
}


