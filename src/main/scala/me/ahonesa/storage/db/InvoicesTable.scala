package me.ahonesa.storage.db

import me.ahonesa.core.models.{Invoice, InvoicePayment, InvoiceStatus, InvoiceSummary}
import com.outworkers.phantom.dsl._
import me.ahonesa.core.models.identifiers.{CustomerId, InvoiceId}
import me.ahonesa.storage._

import scala.concurrent.Future

abstract class InvoicesTable extends Table[InvoicesTable, Invoice] {

  override def tableName: String = "invoices"

  object invoiceId extends StringColumn with PartitionKey
  object customerId extends StringColumn with Index
  object invoiceDate extends DateColumn
  object invoiceSummary extends JsonColumn[InvoiceSummary]
  object invoiceStatus extends StringColumn
  object invoicePayments extends JsonSetColumn[InvoicePayment]

  override def fromRow(r: Row): Invoice = {
    Invoice(
      invoiceId(r),
      customerId(r),
      new java.sql.Date(invoiceDate(r).getTime).toLocalDate,
      invoiceSummary(r),
      InvoiceStatus.fromString(invoiceStatus(r)),
      invoicePayments(r)
    )
  }

  def store(invoice: Invoice) = {
    insert.value(_.invoiceId, invoice.invoiceId)
          .value(_.customerId, invoice.customerId)
          .value(_.invoiceDate, java.sql.Date.valueOf(invoice.invoiceDate))
          .value(_.invoiceSummary, invoice.invoiceSummary)
          .value(_.invoiceStatus, invoice.invoiceStatus.toString)
          .value(_.invoicePayments, invoice.invoicePayments)
  }

  def findById(id: InvoiceId): Future[Option[Invoice]] = {
    select
      .where(_.invoiceId eqs id)
      .one()
  }

  def findByCustomerId(id: CustomerId): Future[List[Invoice]] = {
    select
      .where(_.customerId eqs id)
      .fetch()
  }

}

