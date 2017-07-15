package me.ahonesa.core.models

import java.util.Date

import me.ahonesa.core.models.identifiers.{CustomerId, InvoiceId, PaymentId}
import spray.json._

case class NewInvoice(
  customerId: CustomerId,
  invoiceDate: Date,
  invoiceSummary: InvoiceSummary,
  invoiceStatus: InvoiceStatus
)

case class Invoice(
  invoiceId: InvoiceId,
  customerId: CustomerId,
  invoiceDate: Date,
  invoiceSummary: InvoiceSummary,
  invoiceStatus: InvoiceStatus,
  invoicePayments: Set[InvoicePayment]
)

case class InvoiceSummary(
  invoiceNumber: String,
  toBePaid: BigDecimal,
  alreadyPaid: BigDecimal
)

case class InvoicePayment(
  paymentId: PaymentId,
  paymentDate: Date,
  paymentAmount: BigDecimal
)

sealed trait InvoiceStatus
case object Open extends InvoiceStatus
case object PartiallyPaid extends InvoiceStatus
case object Closed extends InvoiceStatus
case object Error extends InvoiceStatus

object InvoiceStatus {
  def fromString(x: String) = x match {
    case x if x == Open.toString => Open
    case x if x == PartiallyPaid.toString => PartiallyPaid
    case x if x == Closed.toString => Closed
    case _ => Error
  }
}

