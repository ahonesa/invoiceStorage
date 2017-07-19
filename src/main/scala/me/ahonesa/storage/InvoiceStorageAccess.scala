package me.ahonesa.storage

import me.ahonesa.core.models.{Customer, CustomerDetails, Invoice, NewInvoice}
import me.ahonesa.core.models.identifiers.{CustomerId, InvoiceId}

import scala.concurrent.Future

trait InvoiceStorageAccess {
  def findCustomerById(id: CustomerId): Future[Option[Customer]]
  def findInvoiceById(id: InvoiceId): Future[Option[Invoice]]
  def findInvoicesByCustomerId(id: CustomerId): Future[List[Invoice]]
  def createCustomer(id: CustomerId, customerDetails: CustomerDetails): Future[Option[Customer]]
  def createInvoice(id: InvoiceId, newInvoice: NewInvoice): Future[Option[Invoice]]
  def updateInvoice(invoice: Invoice): Future[Option[Invoice]]
}
