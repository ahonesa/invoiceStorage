package me.ahonesa.storage.db

import java.time.chrono.ChronoLocalDate

import me.ahonesa.core.models.{Invoice, InvoicePayment, InvoiceSummary}
import com.outworkers.phantom.dsl._
import me.ahonesa.storage._

import scala.concurrent.Future

abstract class InvoicesTable extends Table[InvoicesTable, Invoice]{

  override def tableName: String = "invoices"

  object invoiceId extends StringColumn with PartitionKey
  object customerId extends StringColumn with Index
  object invoiceDate extends DateColumn
  object invoiceSummary extends JsonColumn[InvoiceSummary]
  object invoiceStatus extends StringColumn
  object invoicePayments extends JsonSetColumn[InvoicePayment]

  def store(invoice: Invoice) = {
    insert.value(_.invoiceId, invoice.invoiceId)
          .value(_.customerId, invoice.customerId)
          .value(_.invoiceDate, invoice.invoiceDate)
          .value(_.invoiceSummary, invoice.invoiceSummary)
          .value(_.invoiceStatus, invoice.invoiceStatus.toString)
          .value(_.invoicePayments, invoice.invoicePayments)
  }
}

