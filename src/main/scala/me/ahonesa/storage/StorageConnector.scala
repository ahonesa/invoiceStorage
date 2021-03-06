package me.ahonesa.storage

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints}
import com.outworkers.phantom.database.Database
import me.ahonesa.core.models._
import me.ahonesa.rest.utils.{Config}
import me.ahonesa.storage.db.{CustomerTable, InvoicesTable}

import scala.concurrent.{ExecutionContext, Future}
import com.outworkers.phantom.dsl._
import com.typesafe.scalalogging.LazyLogging
import me.ahonesa.core.models.identifiers._


trait StorageConnector extends Config with LazyLogging {
  lazy val connector: CassandraConnection = ContactPoints(hosts)
      .withClusterBuilder(_.withCredentials(username, password))
      .keySpace(keyspaceName)
}

class InvoiceStorage(override val connector: CassandraConnection)(implicit executionContext: ExecutionContext)
  extends Database[InvoiceStorage](connector) with Config with RootConnector with InvoiceStorageAccess {

  object CustomerTable extends CustomerTable with connector.Connector
  object InvoicesTable extends InvoicesTable with connector.Connector

  override def findCustomerById(id: CustomerId): Future[Option[Customer]] = {
    CustomerTable.findById(id)
  }

  override def findInvoiceById(id: InvoiceId): Future[Option[Invoice]] = {
    InvoicesTable.findById(id)
  }

  override def findInvoicesByCustomerId(id: CustomerId): Future[List[Invoice]] = {
    InvoicesTable.findByCustomerId(id)
  }

  override def createCustomer(id: CustomerId, customerDetails: CustomerDetails): Future[Option[Customer]] = {
    val customer = Customer(id, customerDetails)
    CustomerTable.store(customer).future().map( resultSet =>
      if(resultSet.wasApplied()) Some(customer) else None
    )
  }

  override def createInvoice(id: InvoiceId, newInvoice: NewInvoice): Future[Option[Invoice]] = {
    val invoiceSummary = InvoiceSummary(newInvoice.invoiceNumber,newInvoice.toBePaid, 0.00)
    val invoice = Invoice(id, newInvoice.customerId, newInvoice.invoiceDate, invoiceSummary, Open, Set())
    InvoicesTable.store(invoice).future().map( resultSet =>
      if(resultSet.wasApplied()) Some(invoice) else None
    )
  }

  override def updateInvoice(invoice: Invoice): Future[Option[Invoice]] = {
    InvoicesTable.store(invoice).future().map( resultSet =>
      if(resultSet.wasApplied()) Some(invoice) else None
    )
  }
}
